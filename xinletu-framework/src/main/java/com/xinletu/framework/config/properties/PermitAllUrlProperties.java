package com.xinletu.framework.config.properties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.xinletu.common.annotation.Anonymous;

/**
 * 设置Anonymous注解允许匿名访问的url
 * 
 * @author ruoyi
 */
@Configuration
public class PermitAllUrlProperties implements InitializingBean, ApplicationContextAware
{
    private ApplicationContext applicationContext;

    private List<String> urls = new ArrayList<>();

    @Override
    public void afterPropertiesSet()
    {
        // 使用 Set 避免重复的 URL
        Set<String> urlSet = new HashSet<>();
        
        // 获取所有的 RequestMappingHandlerMapping
        Map<String, RequestMappingHandlerMapping> mappings = applicationContext.getBeansOfType(RequestMappingHandlerMapping.class);
        
        // 处理每个 RequestMappingHandlerMapping
        mappings.forEach((name, mapping) -> {
            Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
            
            map.forEach((info, handlerMethod) -> {
                // 获取方法上的 Anonymous 注解
                Anonymous method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Anonymous.class);
                if (method != null && info.getPathPatternsCondition() != null) {
                    info.getPathPatternsCondition().getPatterns().forEach(pattern -> {
                        // 获取基本路径，不包含通配符
                        String url = getBasePath(pattern.getPatternString());
                        if (url != null && !url.isEmpty()) {
                            urlSet.add(url);
                        }
                    });
                }
                
                // 获取类上的 Anonymous 注解
                Anonymous controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Anonymous.class);
                if (controller != null && info.getPathPatternsCondition() != null) {
                    info.getPathPatternsCondition().getPatterns().forEach(pattern -> {
                        // 获取基本路径，不包含通配符
                        String url = getBasePath(pattern.getPatternString());
                        if (url != null && !url.isEmpty()) {
                            urlSet.add(url);
                        }
                    });
                }
            });
        });
        
        // 将 Set 转换为 List
        urls.addAll(urlSet);
    }

    /**
     * 获取基本路径，不包含通配符和路径变量
     * 
     * @param url 原始 URL 模式
     * @return 基本路径
     */
    private String getBasePath(String url) {
        // 如果 URL 包含通配符或路径变量，返回基本路径
        int wildcardIndex = url.indexOf('*');
        int variableIndex = url.indexOf('{');
        
        if (wildcardIndex >= 0 || variableIndex >= 0) {
            int endIndex = Math.min(
                wildcardIndex >= 0 ? wildcardIndex : Integer.MAX_VALUE,
                variableIndex >= 0 ? variableIndex : Integer.MAX_VALUE
            );
            
            // 返回基本路径，确保以 / 结尾
            String basePath = url.substring(0, endIndex);
            if (basePath.endsWith("/")) {
                return basePath;
            } else {
                // 找到最后一个 / 的位置
                int lastSlashIndex = basePath.lastIndexOf('/');
                if (lastSlashIndex >= 0) {
                    return basePath.substring(0, lastSlashIndex + 1);
                }
            }
        }
        
        // 如果 URL 不包含通配符或路径变量，直接返回
        return url;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        this.applicationContext = context;
    }

    public List<String> getUrls()
    {
        return urls;
    }

    public void setUrls(List<String> urls)
    {
        this.urls = urls;
    }
}
