package com.example.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Policy {
    private Long id;
    private String name;              // 政策名称
    private String type;              // 政策分类
    private String category;
    private String range;             // 施行范围
    private String document;          // 发文字号
    private String form;              // 颁布形式
    private String organ;             // 制定机关（发文机构）
    private Date viadata;
    private Date pubdata;             // 颁布日期
    private Date perdata;
    private String field;
    private String theme;
    private String keyword;           // 关键词
    private String superior;
    private String precursor;
    private String succeed;
    private String state;             // 时效状态
    private String text;              // 文件正文
    private String pdf;
    private String redundancy;
    private String rank;
    private String policykey;
    private String newrank;
    private String year;
    private String newkey;
    private String secondtheme;
    private Integer allsum;
}
