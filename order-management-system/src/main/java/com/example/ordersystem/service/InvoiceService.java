package com.example.ordersystem.service;

import com.example.ordersystem.entity.Contract;
import com.example.ordersystem.entity.Invoice;
import com.example.ordersystem.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private ContractService contractService;

    // 新增发票（校验金额）
    public String saveInvoice(Invoice invoice) {
        // 1. 查询合同
        Optional<Contract> contractOpt = contractService.getContractByNo(invoice.getContractNo());
        if (!contractOpt.isPresent()) {
            return "合同不存在！";
        }
        Contract contract = contractOpt.get();
        // 2. 校验本次开票金额
        BigDecimal invoiceAmount = invoice.getInvoiceAmount();
        if (invoiceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return "开票金额必须大于0！";
        }
        // 3. 校验累计开票金额不超过合同金额
        BigDecimal remainAmount = contract.getAmount().subtract(contract.getInvoicedAmount());
        if (invoiceAmount.compareTo(remainAmount) > 0) {
            return "本次开票金额超过剩余可开票金额（剩余：" + remainAmount + "）！";
        }
        // 4. 保存发票并更新合同已开票金额
        invoiceRepository.save(invoice);
        contractService.updateInvoicedAmount(invoice.getContractNo(), invoiceAmount);
        return "success";
    }

    // 根据合同编号查询发票
    public List<Invoice> getInvoicesByContractNo(String contractNo) {
        return invoiceRepository.findByContractNo(contractNo);
    }
    // 新增：根据ID查询开票记录
    public Invoice getInvoiceById(Long id) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(id);
        return invoiceOpt.orElse(null); // 存在则返回，不存在返回null
    }


    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

}
