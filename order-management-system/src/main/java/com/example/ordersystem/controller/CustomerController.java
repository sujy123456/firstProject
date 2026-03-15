package com.example.ordersystem.controller;

import com.example.ordersystem.entity.Customer;
import com.example.ordersystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    // 客户申请页面
    @GetMapping("/apply")
    public String apply(Model model, @RequestParam(required = false) Long id) {
        if (id != null) {
            // 修改客户，回显数据
            Optional<Customer> customerOpt = customerService.getCustomerById(id);
            customerOpt.ifPresent(customer -> model.addAttribute("customer", customer));
        } else {
            // 新增客户，空对象
            model.addAttribute("customer", new Customer());
        }
        return "customer/apply"; // 跳转到customer/apply.html
    }

    // 保存客户（新增/修改）
    @PostMapping("/save")
    public String save(Customer customer) {
        // 初始状态为草稿
        if (customer.getStatus() == null || customer.getStatus().isEmpty()) {
            customer.setStatus("草稿");
        }
        customerService.saveCustomer(customer);
        return "redirect:/customer/list"; // 跳转到客户列表
    }

    // 删除客户
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/customer/list";
    }

    // 提交客户申请
    @GetMapping("/submit/{id}")
    public String submit(@PathVariable Long id) {
        customerService.submitCustomer(id);
        return "redirect:/customer/list";
    }

    // 撤销客户申请
    @GetMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id) {
        customerService.cancelCustomer(id);
        return "redirect:/customer/list";
    }

    // 客户审批页面
    @GetMapping("/approve/{id}")
    public String approvePage(@PathVariable Long id, Model model) {
        Optional<Customer> customerOpt = customerService.getCustomerById(id);
        customerOpt.ifPresent(customer -> model.addAttribute("customer", customer));
        return "customer/approve"; // 审批页面
    }

    // 执行审批
    @PostMapping("/approve")
    public String approve(Long id, String status, String approvalRemark) {
        customerService.approveCustomer(id, status, approvalRemark);
        return "redirect:/customer/list";
    }

    // 客户列表/查询
    @GetMapping("/list")
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String type,
            Model model
    ) {
        List<Customer> customers = customerService.searchCustomers(keyword, industry, type);
        model.addAttribute("customers", customers);
        // 回显查询条件
        model.addAttribute("keyword", keyword);
        model.addAttribute("industry", industry);
        model.addAttribute("type", type);
        return "customer/list"; // 客户列表页面
    }

    // 客户选择页面（供合同选择）
    @GetMapping("/select")
    public String select(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customer/select"; // 客户选择弹窗页面
    }
}