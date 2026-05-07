package com.example.service;

import com.example.entity.Policy;

import java.util.List;
import java.util.Map;

public interface PolicyService {
    
    // 查询政策列表（分页）
    Map<String, Object> getPolicyList(String name, String document, 
                                       String organ, String keyword, 
                                       String type, int pageNum, int pageSize);
    
    // 根据ID查询政策详情
    Policy getPolicyById(Long id);
}
