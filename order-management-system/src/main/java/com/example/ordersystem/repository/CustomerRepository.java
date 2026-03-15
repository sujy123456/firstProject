package com.example.ordersystem.repository;

import com.example.ordersystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 客户数据访问层，继承JpaRepository实现基本CRUD
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // 多条件查询客户
    @Query("SELECT c FROM Customer c WHERE " +
            "(c.name LIKE %:keyword% OR c.shortName LIKE %:keyword% OR c.contact LIKE %:keyword% OR c.phone LIKE %:keyword% OR c.address LIKE %:keyword%) " +
            "AND (:industry IS NULL OR c.industry = :industry) " +
            "AND (:type IS NULL OR c.type = :type)")
    List<Customer> searchCustomers(
            @Param("keyword") String keyword,
            @Param("industry") String industry,
            @Param("type") String type
    );
}