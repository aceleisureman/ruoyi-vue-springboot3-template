package com.xinletu.web.core.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xinletu.common.config.RuoYiConfig;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Swagger配置
 * 
 * @author ruoyi
 */
@Configuration
public class SwaggerConfig
{
    /** 系统基础配置 */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    /**
     * 创建API
     */
    @Bean
    public OpenAPI openAPI()
    {
        OpenAPI openAPI = new OpenAPI()
            .info(new Info()
                .title("标题：鑫乐途管理系统_接口文档")
                .description("描述：用于管理集团旗下公司的人员信息,具体包括XXX,XXX模块...")
                .version("版本号:" + ruoyiConfig.getVersion())
                .contact(new Contact()
                    .name(ruoyiConfig.getName())
                    .email(null)
                    .url(null)));
                    
        // 添加安全配置
        openAPI.addSecurityItem(new SecurityRequirement().addList("Authorization"))
            .components(new Components()
                .addSecuritySchemes("Authorization", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")));
                        
        return openAPI;
    }
    
    /**
     * 全部接口
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("所有接口")
                .pathsToMatch("/**")
                .build();
    }
    
    /**
     * 系统接口分组
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("系统接口")
                .pathsToMatch("/system/**")
                .build();
    }
    
    /**
     * 监控接口分组
     */
    @Bean
    public GroupedOpenApi monitorApi() {
        return GroupedOpenApi.builder()
                .group("监控接口")
                .pathsToMatch("/monitor/**")
                .build();
    }
    
    /**
     * 工具接口分组
     */
    @Bean
    public GroupedOpenApi toolApi() {
        return GroupedOpenApi.builder()
                .group("工具接口")
                .pathsToMatch("/tool/**")
                .build();
    }
    
    /**
     * 测试接口分组
     */
    @Bean
    public GroupedOpenApi testApi() {
        return GroupedOpenApi.builder()
                .group("测试接口")
                .pathsToMatch("/admin/test/**")
                .build();
    }
}
