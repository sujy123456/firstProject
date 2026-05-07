package com.example.controller;

import com.example.entity.Policy;
import com.example.entity.PolicyKind;
import com.example.service.PolicyKindService;
import com.example.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "*", allowCredentials = "false")
@RestController
@RequestMapping("/api")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private PolicyKindService policyKindService;
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PolicyController.class);

    // 获取政策分类树
    @GetMapping("/policyKinds")
    public Map<String, Object> getPolicyKinds() {
        logger.info("接收到获取政策分类请求");
        Map<String, Object> result = new HashMap<>();
        try {
            List<PolicyKind> policyKinds = policyKindService.getAllPolicyKinds();
            logger.info("查询到政策分类数量: {}", policyKinds.size());
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", policyKinds);
        } catch (Exception e) {
            logger.error("获取政策分类失败: ", e);
            result.put("code", 500);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return result;
    }

    // 查询政策列表（分页）
    @GetMapping("/policies")
    public Map<String, Object> getPolicyList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String document,
            @RequestParam(required = false) String organ,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        logger.info("接收到查询政策列表请求，参数: name={}, document={}, organ={}, keyword={}, type={}, pageNum={}, pageSize={}", 
                   name, document, organ, keyword, type, pageNum, pageSize);
        
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> data = policyService.getPolicyList(
                    name, document, organ, keyword, type, pageNum, pageSize);
            logger.info("查询到政策列表，总数: {}", data.get("total"));
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", data);
        } catch (Exception e) {
            logger.error("查询政策列表失败: ", e);
            result.put("code", 500);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return result;
    }

    // 获取政策详情
    @GetMapping("/policy/{id}")
    public Map<String, Object> getPolicyDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Policy policy = policyService.getPolicyById(id);
            if (policy != null) {
                result.put("code", 200);
                result.put("message", "success");
                result.put("data", policy);
            } else {
                result.put("code", 404);
                result.put("message", "政策不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return result;
    }
}