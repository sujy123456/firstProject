package com.example.entity;

import lombok.Data;

@Data
public class PolicyKind {
    private String type;           // 分类名称
    private Integer policyCount;   // 政策数量
}
