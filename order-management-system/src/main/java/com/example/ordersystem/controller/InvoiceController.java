//package com.example.ordersystem.controller;
//
//import com.example.ordersystem.entity.Contract;
//import com.example.ordersystem.entity.Invoice;
//import com.example.ordersystem.service.ContractService;
//import com.example.ordersystem.service.InvoiceService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/invoice")
//public class InvoiceController {
//    @Autowired
//    private InvoiceService invoiceService;
//    @Autowired
//    private ContractService contractService;
//
//    // 开票页面
//    @GetMapping("/input")
//    public String input(Model model, @RequestParam(required = false) String contractNo) {
//        model.addAttribute("contractNo", contractNo);
//        return "invoice/input"; // 开票录入页面
//    }
//
//
//
//    // 保存发票
//    @PostMapping("/save")
//    public String save(Invoice invoice, Model model) {
//        String result = invoiceService.saveInvoice(invoice);
//        if ("success".equals(result)) {
//            return "redirect:/invoice/list?contractNo=" + invoice.getContractNo();
//        } else {
//            // 校验失败，返回错误信息
//            model.addAttribute("error", result);
//            model.addAttribute("invoice", invoice);
//            return "invoice/input";
//        }
//    }
//
//    // 发票列表
//    @GetMapping("/list")
//    public String list(@RequestParam(required = false) String contractNo, Model model) {
//        List<Invoice> invoices = invoiceService.getInvoicesByContractNo(contractNo);
//        model.addAttribute("invoices", invoices);
//        model.addAttribute("contractNo", contractNo);
//        // 查询合同信息（显示合同金额）
//        Optional<Contract> contractOpt = contractService.getContractByNo(contractNo);
//        contractOpt.ifPresent(contract -> model.addAttribute("contract", contract));
//        return "invoice/list";
//    }
//
//    // 欠票查询页面
//    @GetMapping("/uninvoiced")
//    public String uninvoiced(@RequestParam(required = false) String keyword, Model model) {
//        List<Contract> contracts = contractService.getUninvoicedContracts(keyword);
//        model.addAttribute("contracts", contracts);
//        model.addAttribute("keyword", keyword);
//        return "invoice/uninvoiced"; // 欠票查询页面
//    }
//}
package com.example.ordersystem.controller;

import com.example.ordersystem.entity.Contract;
import com.example.ordersystem.entity.Invoice;
import com.example.ordersystem.service.ContractService;
import com.example.ordersystem.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private ContractService contractService;


    // 极简版input方法 - 保证invoice绝对不为null
    @GetMapping("/input")
    public String input(Model model,
                        @RequestParam(required = false) String contractNo,
                        @RequestParam(required = false) Long id) {

        // 1. 强制初始化invoice，绝对不为null
        Invoice invoice = new Invoice();

        // 2. 编辑模式：只有id有效且查询到数据才赋值
        if (id != null) {
            Invoice temp = invoiceService.getInvoiceById(id);
            if (temp != null) {
                invoice = temp;
            }
        }

        // 3. 从合同跳转：只赋值合同编号，不做复杂计算（先恢复功能）
        if (contractNo != null && !contractNo.isEmpty()) {
            invoice.setContractNo(contractNo);
        }

        // 4. 强制放入Model，键名必须是"invoice"，值绝对不为null
        model.addAttribute("invoice", invoice);
        model.addAttribute("remainAmount", BigDecimal.ZERO); // 先设0，能打开页面就行
        model.addAttribute("errorMsg", "");

        return "invoice/input";
    }

    // 其他方法（save/list/delete）暂时保留，但save方法简化如下：
    @PostMapping("/save")
    public String save(Invoice invoice) {
        // 极简保存逻辑 - 先能保存，后续再加校验
        if (invoice == null) {
            invoice = new Invoice();
        }
        // 自动填充日期（如果前端没传）
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(new java.util.Date());
        }
        // 保存开票记录
        invoiceService.saveInvoice(invoice);

        // 简单更新合同已开票金额（后续再优化）
        if (invoice.getContractNo() != null) {
            Optional<Contract> contractOpt = contractService.getContractByNo(invoice.getContractNo());
            if (contractOpt.isPresent()) {
                Contract contract = contractOpt.get();
                BigDecimal invoiced = contract.getInvoicedAmount() == null ? BigDecimal.ZERO : contract.getInvoicedAmount();
                contract.setInvoicedAmount(invoiced.add(invoice.getInvoiceAmount()));
                contractService.saveContract(contract);
            }
        }

        return "redirect:/invoice/list";
    }

    // list方法也简化（保证能显示列表）
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("invoices", invoiceService.getAllInvoices() != null ? invoiceService.getAllInvoices() : java.util.Collections.emptyList());
        return "invoice/list";
    }
    // 未开票合同查询（修正版）
    @GetMapping("/uninvoiced")
    public String uninvoiced(
            @RequestParam(required = false) String contractNo,
            @RequestParam(required = false) String customerName,
            Model model
    ) {
        // 1. 查询合同列表（先显示所有，不做过滤，排查数据问题）
        List<Contract> contractList;
        if ((contractNo != null && !contractNo.isEmpty()) || (customerName != null && !customerName.isEmpty())) {
            contractList = contractService.searchContracts(contractNo, customerName);
        } else {
            // 先查询所有合同，确保有数据显示
            contractList = contractService.getAllContracts();
        }
        // 兜底：避免null
        if (contractList == null) {
            contractList = new ArrayList<>();
        }

        // 2. 简化未开票过滤逻辑（添加日志，排查数据）
        List<Contract> uninvoicedList = new ArrayList<>();
        for (Contract contract : contractList) {
            // 打印每个合同的关键数据，排查过滤逻辑
            System.out.println("合同编号：" + contract.getContractNo() +
                    "，客户名称：" + contract.getCustomerName() +
                    "，总金额：" + contract.getAmount() +
                    "，已开票金额：" + contract.getInvoicedAmount());

            // 修正过滤逻辑：兼容金额为null的情况，且只过滤“已开票金额=总金额”的合同
            BigDecimal totalAmount = contract.getAmount() != null ? contract.getAmount() : BigDecimal.ZERO;
            BigDecimal invoicedAmount = contract.getInvoicedAmount() != null ? contract.getInvoicedAmount() : BigDecimal.ZERO;

            // 条件改为：总金额>0 且 已开票金额 < 总金额（避免过滤掉未开票的新合同）
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0 && invoicedAmount.compareTo(totalAmount) < 0) {
                // 新增：单独存储剩余金额，避免覆盖原有invoicedAmount字段
                model.addAttribute("remain_" + contract.getId(), totalAmount.subtract(invoicedAmount));
                uninvoicedList.add(contract);
            } else {
                // 即使是已开完票的合同，也可选加入列表（方便排查）
                // uninvoicedList.add(contract); // 临时放开，看是否有数据
            }
        }

        // 3. 传递数据到模板（新增：打印最终列表数量）
        System.out.println("未开票合同数量：" + uninvoicedList.size());
        model.addAttribute("uninvoicedList", uninvoicedList);
        model.addAttribute("contractNo", contractNo != null ? contractNo : "");
        model.addAttribute("customerName", customerName != null ? customerName : "");

        return "invoice/uninvoiced";
    }
}
