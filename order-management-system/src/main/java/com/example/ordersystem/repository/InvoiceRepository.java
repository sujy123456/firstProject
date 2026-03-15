package com.example.ordersystem.repository;

import com.example.ordersystem.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // 根据合同编号查询发票
    List<Invoice> findByContractNo(String contractNo);
}