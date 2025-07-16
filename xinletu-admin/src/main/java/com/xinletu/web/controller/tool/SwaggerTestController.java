package com.xinletu.web.controller.tool;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xinletu.common.core.domain.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Swagger测试接口
 * 
 * @author xinletu
 */
@Tag(name = "Swagger测试接口", description = "用于测试Swagger是否正常工作")
@RestController
@RequestMapping("/tool/swagger")
public class SwaggerTestController
{
    @Operation(summary = "测试接口", description = "用于测试Swagger是否正常工作")
    @GetMapping("/test")
    public R<String> test()
    {
        return R.ok("Swagger测试接口调用成功");
    }
} 