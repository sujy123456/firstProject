package com.example.ordersystem.service;

import com.example.ordersystem.entity.Customer;
import com.example.ordersystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    // 新增/修改客户
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // 删除客户
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    // 根据ID查询客户
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // 多条件查询客户
    public List<Customer> searchCustomers(String keyword, String industry, String type) {
        // 处理空值，避免SQL查询异常
        if (keyword == null) keyword = "";
        if (industry != null && industry.isEmpty()) industry = null;
        if (type != null && type.isEmpty()) type = null;
        return customerRepository.searchCustomers(keyword, industry, type);
    }

    // 提交客户申请（修改状态为已提交）
    public Customer submitCustomer(Long id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setStatus("已提交");
            return customerRepository.save(customer);
        }
        return null;
    }

    // 撤销客户申请（改回草稿）
    public Customer cancelCustomer(Long id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setStatus("草稿");
            return customerRepository.save(customer);
        }
        return null;
    }

    // 审批客户申请
    public Customer approveCustomer(Long id, String status, String remark) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setStatus(status); // 已审批/已驳回
            customer.setApprovalRemark(remark);
            return customerRepository.save(customer);
        }
        return null;
    }

    // 查询所有客户（用于合同选择客户）
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}