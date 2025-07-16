package com.xinletu.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xinletu.common.core.domain.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 测试控制器
 * 
 * @author xinletu
 */
@Tag(name = "核心模块测试接口")
@RestController
@RequestMapping("/api/v1/core/test")
public class TestCoreController
{
    /**
     * 测试方法
     */
    @Operation(summary = "测试方法")
    @GetMapping("/hello")
    public AjaxResult hello()
    {
        return AjaxResult.success("Hello Core Module!");
    }
} 