package com.example.service.impl;

import com.example.entity.Policy;
import com.example.mapper.PolicyMapper;
import com.example.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyMapper policyMapper;

    @Override
    public Map<String, Object> getPolicyList(String name, String document,
                                              String organ, String keyword,
                                              String type, int pageNum, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        int offset = (pageNum - 1) * pageSize;
        
        // 查询政策列表
        List<Policy> policyList = policyMapper.selectPolicyList(
                name, document, organ, keyword, type, offset, pageSize);
        
        // 查询总数
        int total = policyMapper.countPolicyList(
                name, document, organ, keyword, type);
        
        result.put("list", policyList);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (total + pageSize - 1) / pageSize);
        
        return result;
    }

    @Override
    public Policy getPolicyById(Long id) {
        return policyMapper.selectPolicyById(id);
    }
}
