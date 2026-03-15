package com.example.ordersystem.controller;

import com.example.ordersystem.entity.Contract;
import com.example.ordersystem.entity.Customer;
import com.example.ordersystem.service.ContractService;
import com.example.ordersystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/contract")
public class ContractController {
    @Autowired
    private ContractService contractService;
    @Autowired
    private CustomerService customerService;

    // 合同录入页面
    @GetMapping("/input")
    public String input(Model model, @RequestParam(required = false) Long id) {
        if (id != null) {
            // 修改合同，回显数据
            Optional<Contract> contractOpt = contractService.getContractById(id);
            contractOpt.ifPresent(contract -> model.addAttribute("contract", contract));
        } else {
            // 新增合同，空对象
            model.addAttribute("contract", new Contract());
        }
        return "contract/input"; // 合同录入页面
    }

    // 保存合同（新增/修改）
    @PostMapping("/save")
    public String save(Contract contract) {
        contractService.saveContract(contract);
        return "redirect:/contract/list"; // 跳转到合同列表
    }

    // 删除合同
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        contractService.deleteContract(id);
        return "redirect:/contract/list";
    }

    // 提交合同
    @GetMapping("/submit/{id}")
    public String submit(@PathVariable Long id) {
        contractService.submitContract(id);
        return "redirect:/contract/list";
    }

    // 撤销合同
    @GetMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id) {
        contractService.cancelContract(id);
        return "redirect:/contract/list";
    }

    // 合同审批页面
    @GetMapping("/approve/{id}")
    public String approvePage(@PathVariable Long id, Model model) {
        Optional<Contract> contractOpt = contractService.getContractById(id);
        contractOpt.ifPresent(contract -> model.addAttribute("contract", contract));
        return "contract/approve"; // 合同审批页面
    }

    // 执行合同审批
    @PostMapping("/approve")
    public String approve(Long id, String status, String approvalRemark) {
        contractService.approveContract(id, status, approvalRemark);
        return "redirect:/contract/list";
    }

    // 合同列表
    @GetMapping("/list")
    public String list(Model model) {
        List<Contract> contracts = contractService.getUninvoicedContracts("");
        model.addAttribute("contracts", contracts);
        return "contract/list";
    }

    // 选择客户后返回合同页面（回填客户信息）
    @GetMapping("/selectCustomer")
    public String selectCustomer(Long customerId, Model model) {
        Optional<Customer> customerOpt = customerService.getCustomerById(customerId);
        Contract contract = new Contract();
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            contract.setCustomerId(customerId);
            contract.setCustomerName(customer.getName());
        }
        model.addAttribute("contract", contract);
        return "contract/input";
    }
}