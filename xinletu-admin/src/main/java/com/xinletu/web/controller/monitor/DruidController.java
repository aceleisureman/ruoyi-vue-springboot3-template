package com.xinletu.web.controller.monitor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * druid 监控
 * 
 * @author ruoyi
 */
@Controller
public class DruidController
{
    /**
     * 跳转到druid监控页
     */
    @GetMapping("/druid")
    public String index()
    {
        return "redirect:/druid/index.html";
    }
} 