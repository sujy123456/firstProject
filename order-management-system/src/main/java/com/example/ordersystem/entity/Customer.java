package com.example.ordersystem.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 客户实体类，对应数据库customer表
 */
@Data  // Lombok注解，自动生成get/set/toString等方法
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;          // 主键ID
    private String name;      // 客户名称
    private String shortName; // 客户简称
    private String industry;  // 行业（选择框）
    private String type;      // 客户类型（选择框）
    private String contact;   // 联系人
    private String phone;     // 联系电话
    private String address;   // 客户地址
    private String status;    // 审批状态：草稿、已提交、已审批、已驳回
    private String approvalRemark; // 审批备注
}