package com.example.ordersystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器，跳转系统主页面
 */
@Controller
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "index"; // 跳转到templates/index.html
    }
}