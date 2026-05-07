package com.example.service;

import com.example.entity.PolicyKind;

import java.util.List;

public interface PolicyKindService {
    
    // 查询所有政策分类及政策数量
    List<PolicyKind> getAllPolicyKinds();
}
