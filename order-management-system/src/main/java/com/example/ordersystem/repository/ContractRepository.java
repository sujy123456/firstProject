package com.example.ordersystem.repository;

import com.example.ordersystem.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    // 根据合同编号查询
    Optional<Contract> findByContractNo(String contractNo);

    // 查询最大流水号（按年月）
    @Query("SELECT MAX(CAST(SUBSTRING(c.contractNo, 8) AS INTEGER)) FROM Contract c WHERE SUBSTRING(c.contractNo, 2, 6) = :yearMonth")
    Integer findMaxSerialNo(@Param("yearMonth") String yearMonth);

    // 欠票查询（已开票/未开票）
    @Query("SELECT c FROM Contract c WHERE c.contractNo LIKE %:keyword% OR c.customerName LIKE %:keyword%")
    List<Contract> findUninvoicedContracts(@Param("keyword") String keyword);
    // 按合同编号模糊查询
    List<Contract> findByContractNoLike(String contractNo);
    // 按客户名称模糊查询
    List<Contract> findByCustomerNameLike(String customerName);
}