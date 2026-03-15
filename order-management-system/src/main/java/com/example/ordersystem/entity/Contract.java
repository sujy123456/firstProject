package com.example.ordersystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合同实体类，对应数据库contract表
 */
@Data
@Entity
@Table(name = "contract")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 主键ID
    private String contractNo;      // 合同编号（系统生成）
    private String contractName;    // 合同名称
//    private Date signDate;          // 签订日期
//    private Date deliveryDate;      // 交货日期
    // 关键修复：使用LocalDate + 日期格式化注解
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 适配前端提交的日期格式
    private LocalDate signDate;          // 签订日期

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;      // 交货日期
    private String contractType;    // 合同类型（选择框）
    private BigDecimal amount;      // 合同金额
    private Long customerId;        // 关联客户ID
    private String customerName;    // 客户名称（冗余存储，方便显示）
    private String status;          // 审批状态：草稿、已提交、已审批、已驳回
    private String approvalRemark;  // 审批备注
    private BigDecimal invoicedAmount; // 已开票金额（初始0）
}