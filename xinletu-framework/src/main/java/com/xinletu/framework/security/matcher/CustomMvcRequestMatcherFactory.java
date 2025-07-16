package com.xinletu.framework.security.matcher;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * 自定义 MvcRequestMatcher 工厂类
 * 
 * @author ruoyi获取用户信息
 */
public class CustomMvcRequestMatcherFactory {
    
    private final HandlerMappingIntrospector introspector;
    
    public CustomMvcRequestMatcherFactory(HandlerMappingIntrospector introspector) {
        this.introspector = introspector;
    }
    
    public RequestMatcher createRequestMatcher(String pattern) {
        return new CustomMvcRequestMatcher(introspector, pattern);
    }
} 