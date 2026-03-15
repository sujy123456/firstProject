package com.example.ordersystem.service;

import com.example.ordersystem.entity.Contract;
import com.example.ordersystem.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ContractService {
    @Autowired
    private ContractRepository contractRepository;


    // 生成合同编号：前缀+年+月+3位流水号
    private String generateContractNo(String contractType) {
        // 1. 获取前缀（合同类型对应，如产品销售合同=C）
        String prefix = getPrefixByType(contractType);
        // 2. 获取当前年月（yyyyMM）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String yearMonth = sdf.format(new Date());
        // 3. 查询当月最大流水号
        Integer maxSerial = contractRepository.findMaxSerialNo(yearMonth);
        int serial = (maxSerial == null) ? 1 : maxSerial + 1;
        // 4. 拼接编号（流水号补0到3位）
        return prefix + yearMonth + String.format("%03d", serial);
    }

    // 合同类型对应前缀（可扩展）
    private String getPrefixByType(String contractType) {
        switch (contractType) {
            case "产品销售合同": return "C";
            case "服务合同": return "F";
            default: return "O"; // 其他类型默认O
        }
    }

    // 新增/修改合同（新增时自动生成编号）
    public Contract saveContract(Contract contract) {
        if (contract.getContractNo() == null || contract.getContractNo().isEmpty()) {
            // 新增合同，生成编号
            contract.setContractNo(generateContractNo(contract.getContractType()));
            // 初始化已开票金额为0
            contract.setInvoicedAmount(BigDecimal.ZERO);
            // 初始状态为草稿
            contract.setStatus("草稿");
        }
        return contractRepository.save(contract);
    }

    // 删除合同
    public void deleteContract(Long id) {
        contractRepository.deleteById(id);
    }

    // 根据ID查询合同
    public Optional<Contract> getContractById(Long id) {
        return contractRepository.findById(id);
    }

    // 根据编号查询合同
    public Optional<Contract> getContractByNo(String contractNo) {
        return contractRepository.findByContractNo(contractNo);
    }

    // 提交合同（改状态为已提交）
    public Contract submitContract(Long id) {
        Optional<Contract> contractOpt = contractRepository.findById(id);
        if (contractOpt.isPresent()) {
            Contract contract = contractOpt.get();
            contract.setStatus("已提交");
            return contractRepository.save(contract);
        }
        return null;
    }

    // 撤销合同（改回草稿）
    public Contract cancelContract(Long id) {
        Optional<Contract> contractOpt = contractRepository.findById(id);
        if (contractOpt.isPresent()) {
            Contract contract = contractOpt.get();
            contract.setStatus("草稿");
            return contractRepository.save(contract);
        }
        return null;
    }

    // 审批合同
    public Contract approveContract(Long id, String status, String remark) {
        Optional<Contract> contractOpt = contractRepository.findById(id);
        if (contractOpt.isPresent()) {
            Contract contract = contractOpt.get();
            contract.setStatus(status);
            contract.setApprovalRemark(remark);
            return contractRepository.save(contract);
        }
        return null;
    }

    // 欠票查询
    public List<Contract> getUninvoicedContracts(String keyword) {
        if (keyword == null) keyword = "";
        return contractRepository.findUninvoicedContracts(keyword);
    }

    // 更新合同已开票金额
    public void updateInvoicedAmount(String contractNo, BigDecimal addAmount) {
        Optional<Contract> contractOpt = contractRepository.findByContractNo(contractNo);
        if (contractOpt.isPresent()) {
            Contract contract = contractOpt.get();
            BigDecimal newInvoiced = contract.getInvoicedAmount().add(addAmount);
            contract.setInvoicedAmount(newInvoiced);
            contractRepository.save(contract);
        }
    }

/**
 * 根据合同编号和客户名称搜索合同的方法
 * @param contractNo 合同编号，用于精确匹配或模糊搜索合同
 * @param customerName 客户名称，用于精确匹配或模糊搜索合同
 * @return 返回符合条件的合同列表，如果没有匹配项则返回空列表
 */
// 1. 新增：按合同编号和客户名称模糊查询合同
public List<Contract> searchContracts(String contractNo, String customerName) {
    if (contractNo != null && !contractNo.isEmpty()) {
        return contractRepository.findByContractNoLike("%" + contractNo + "%");
    } else if (customerName != null && !customerName.isEmpty()) {
        return contractRepository.findByCustomerNameLike("%" + customerName + "%");
    } else {
        return Collections.emptyList();
    }
}

    // 2. 新增：查询所有合同
    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }
}