package com.xinletu.framework.security.matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 自定义 MvcRequestMatcher，添加日志功能
 * 
 * @author ruoyi
 */
public class CustomMvcRequestMatcher implements RequestMatcher {
    
    private static final Logger log = LoggerFactory.getLogger(CustomMvcRequestMatcher.class);
    
    private final String pattern;
    private final HandlerMappingIntrospector introspector;
    private MvcRequestMatcher delegate;
    
    public CustomMvcRequestMatcher(HandlerMappingIntrospector introspector, String pattern) {
        this.pattern = pattern;
        this.introspector = introspector;
        try {
            this.delegate = new MvcRequestMatcher(introspector, pattern);
        } catch (Exception e) {
            log.error("Error creating MvcRequestMatcher for pattern: {}", pattern, e);
        }
    }
    
    @Override
    public boolean matches(HttpServletRequest request) {
        try {
            if (delegate == null) {
                log.warn("Delegate MvcRequestMatcher is null for pattern: {}, request: {}", pattern, request.getRequestURI());
                return false;
            }
            
            boolean matches = delegate.matches(request);
            log.info("Request '{}' {} pattern '{}'", request.getRequestURI(), matches ? "matches" : "does not match", pattern);
            return matches;
        } catch (Exception e) {
            log.error("Error matching request '{}' against pattern '{}'", request.getRequestURI(), pattern, e);
            return false;
        }
    }
} 