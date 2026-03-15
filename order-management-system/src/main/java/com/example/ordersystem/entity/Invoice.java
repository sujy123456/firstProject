//package com.example.ordersystem.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import java.math.BigDecimal;
//import java.util.Date;
//
///**
// * 发票实体类，对应数据库invoice表
// */
//@Data
//@Entity
//@Table(name = "invoice")
//@EntityListeners(jakarta.persistence.PrePersist.class)
//public class Invoice {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;                // 主键ID
//    private String contractNo;      // 关联合同编号
//    private BigDecimal invoiceAmount; // 本次开票金额
//    private Date invoiceDate;       // 开票日期
//    private String remark;          // 备注
//
//    // 自动填充开票日期
//    @PrePersist
//    public void prePersist() {
//        if (this.invoiceDate == null) {
//            this.invoiceDate = new Date();
//        }
//    }
//}
package com.example.ordersystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "invoice")
@EntityListeners(jakarta.persistence.PrePersist.class)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 主键ID
    private String contractNo;      // 关联合同编号
    private BigDecimal invoiceAmount; // 本次开票金额

    // 简化日期格式：适配<input type="date">的yyyy-MM-dd
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date invoiceDate;       // 开票日期
    private String remark;          // 备注

    // 自动填充开票日期
    @PrePersist
    public void prePersist() {
        if (this.invoiceDate == null) {
            this.invoiceDate = new Date();
        }
    }
}