package com.example.service.impl;

import com.example.entity.PolicyKind;
import com.example.mapper.PolicyKindMapper;
import com.example.service.PolicyKindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyKindServiceImpl implements PolicyKindService {

    @Autowired
    private PolicyKindMapper policyKindMapper;

    @Override
    public List<PolicyKind> getAllPolicyKinds() {
        return policyKindMapper.selectAllPolicyKinds();
    }
}
