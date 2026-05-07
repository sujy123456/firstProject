package com.example.mapper;

import com.example.entity.Policy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PolicyMapper {
    
    // 查询政策列表（分页）
    List<Policy> selectPolicyList(@Param("name") String name,
                                   @Param("document") String document,
                                   @Param("organ") String organ,
                                   @Param("keyword") String keyword,
                                   @Param("type") String type,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);
    
    // 查询政策总数
    int countPolicyList(@Param("name") String name,
                        @Param("document") String document,
                        @Param("organ") String organ,
                        @Param("keyword") String keyword,
                        @Param("type") String type);
    
    // 根据ID查询政策详情
    Policy selectPolicyById(Long id);
}
