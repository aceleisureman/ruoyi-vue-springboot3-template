package com.xinletu.framework.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.support.jakarta.StatViewServlet;
import com.alibaba.druid.support.jakarta.WebStatFilter;
import com.alibaba.druid.util.Utils;
import com.xinletu.common.enums.DataSourceType;
import com.xinletu.common.utils.spring.SpringUtils;
import com.xinletu.framework.config.properties.DruidProperties;
import com.xinletu.framework.config.properties.DruidStatProperties;
import com.xinletu.framework.datasource.DynamicDataSource;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * druid 配置多数据源
 * 
 * @author xinletu
 */
@Configuration
public class DruidConfig
{
    @Bean
    @ConfigurationProperties("spring.datasource.druid.master")
    public DataSource masterDataSource(DruidProperties druidProperties)
    {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.slave")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource(DruidProperties druidProperties)
    {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dataSource(DataSource masterDataSource)
    {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.MASTER.name(), masterDataSource);
        setDataSource(targetDataSources, DataSourceType.SLAVE.name(), "slaveDataSource");
        return new DynamicDataSource(masterDataSource, targetDataSources);
    }
    
    /**
     * 设置数据源
     * 
     * @param targetDataSources 备选数据源集合
     * @param sourceName 数据源名称
     * @param beanName bean名称
     */
    public void setDataSource(Map<Object, Object> targetDataSources, String sourceName, String beanName)
    {
        try
        {
            DataSource dataSource = SpringUtils.getBean(beanName);
            targetDataSources.put(sourceName, dataSource);
        }
        catch (Exception e)
        {
        }
    }
    
    /**
     * 注册StatViewServlet
     */
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true")
    public ServletRegistrationBean<StatViewServlet> druidStatViewServlet(DruidStatProperties properties)
    {
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>();
        servletRegistrationBean.setServlet(new StatViewServlet());
        servletRegistrationBean.addUrlMappings(config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*");
        
        // 添加IP白名单
        if (StringUtils.hasText(config.getAllow())) {
            servletRegistrationBean.addInitParameter("allow", config.getAllow());
        }
        // 添加IP黑名单，deny优先于allow
        if (StringUtils.hasText(config.getDeny())) {
            servletRegistrationBean.addInitParameter("deny", config.getDeny());
        }
        // 添加控制台管理用户
        if (StringUtils.hasText(config.getLoginUsername())) {
            servletRegistrationBean.addInitParameter("loginUsername", config.getLoginUsername());
        }
        if (StringUtils.hasText(config.getLoginPassword())) {
            servletRegistrationBean.addInitParameter("loginPassword", config.getLoginPassword());
        }
        // 是否能够重置数据
        if (StringUtils.hasText(config.getResetEnable())) {
            servletRegistrationBean.addInitParameter("resetEnable", config.getResetEnable());
        }
        return servletRegistrationBean;
    }

    /**
     * 注册WebStatFilter
     */
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.webStatFilter.enabled", havingValue = "true")
    public FilterRegistrationBean<WebStatFilter> webStatFilter(DruidStatProperties properties)
    {
        DruidStatProperties.WebStatFilter config = properties.getWebStatFilter();
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns(config.getUrlPattern() != null ? config.getUrlPattern() : "/*");
        
        if (StringUtils.hasText(config.getExclusions())) {
            filterRegistrationBean.addInitParameter("exclusions", config.getExclusions());
        } else {
            filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        }
        
        if (StringUtils.hasText(config.getSessionStatEnable())) {
            filterRegistrationBean.addInitParameter("sessionStatEnable", config.getSessionStatEnable());
        }
        if (StringUtils.hasText(config.getSessionStatMaxCount())) {
            filterRegistrationBean.addInitParameter("sessionStatMaxCount", config.getSessionStatMaxCount());
        }
        if (StringUtils.hasText(config.getPrincipalSessionName())) {
            filterRegistrationBean.addInitParameter("principalSessionName", config.getPrincipalSessionName());
        }
        if (StringUtils.hasText(config.getPrincipalCookieName())) {
            filterRegistrationBean.addInitParameter("principalCookieName", config.getPrincipalCookieName());
        }
        if (StringUtils.hasText(config.getProfileEnable())) {
            filterRegistrationBean.addInitParameter("profileEnable", config.getProfileEnable());
        }
        return filterRegistrationBean;
    }

    /**
     * 去除监控页面底部的广告
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true")
    public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties)
    {
        // 获取web监控页面的参数
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        // 提取common.js的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        final String filePath = "support/http/resources/js/common.js";
        // 创建filter进行过滤
        Filter filter = new Filter()
        {
            @Override
            public void init(jakarta.servlet.FilterConfig filterConfig) throws ServletException
            {
            }
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException
            {
                chain.doFilter(request, response);
                // 重置缓冲区，响应头不会被重置
                response.resetBuffer();
                // 获取common.js
                String text = Utils.readFromResource(filePath);
                // 正则替换banner, 除去底部的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                response.getWriter().write(text);
            }
            @Override
            public void destroy()
            {
            }
        };
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }
}
