package com.example.mapper;

import com.example.entity.PolicyKind;

import java.util.List;

public interface PolicyKindMapper {
    
    // 查询所有政策分类及政策数量
    List<PolicyKind> selectAllPolicyKinds();
    
    // 根据分类名称查询分类信息
    PolicyKind selectPolicyKindById(String type);
}
