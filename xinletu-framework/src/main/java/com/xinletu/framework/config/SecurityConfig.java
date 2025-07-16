package com.xinletu.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.xinletu.framework.config.properties.PermitAllUrlProperties;
import com.xinletu.framework.security.filter.JwtAuthenticationTokenFilter;
import com.xinletu.framework.security.handle.AuthenticationEntryPointImpl;
import com.xinletu.framework.security.handle.LogoutSuccessHandlerImpl;
import com.xinletu.framework.security.matcher.CustomMvcRequestMatcherFactory;

/**
 * spring security配置
 *
 * @author ruoyi
 */
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * 自定义用户认证逻辑
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 认证失败处理类
     */
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * 退出处理类
     */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /**
     * token认证过滤器
     */
    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    /**
     * 跨域过滤器
     */
    @Autowired
    private CorsFilter corsFilter;

    /**
     * 允许匿名访问的地址
     */
    @Autowired
    private PermitAllUrlProperties permitAllUrl;

    /**
     * 处理程序映射内省器
     */
    @Autowired
    private HandlerMappingIntrospector handlerMappingIntrospector;

    /**
     * 身份验证实现
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Configuring SecurityFilterChain");

        // 创建自定义 MvcRequestMatcher 工厂
        CustomMvcRequestMatcherFactory matcherFactory = new CustomMvcRequestMatcherFactory(handlerMappingIntrospector);

        return httpSecurity
                // CSRF禁用，因为不使用session
                .csrf(csrf -> csrf.disable())
                // 禁用HTTP响应标头
                .headers((headersCustomizer) -> {
                    headersCustomizer.cacheControl(cache -> cache.disable()).frameOptions(options -> options.sameOrigin());
                })
                // 认证失败处理类
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // 基于token，所以不需要session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 注解标记允许匿名访问的url
                .authorizeHttpRequests((requests) -> {
                    // 不再使用 permitAllUrl.getUrls()，改为手动配置
                    log.info("Configuring authorizeHttpRequests");

                    // 使用 AntPathRequestMatcher 替代 MvcRequestMatcher
                    try {
                        // 对于登录login 注册register 验证码captchaImage 允许匿名访问
                        requests.requestMatchers(new AntPathRequestMatcher("/admin/login")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/admin/register")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/admin/captchaImage")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/admin/getInfo")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/admin/getRouters")).permitAll()
                                // SpringDoc OpenAPI 相关路径
                                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html")).permitAll()
                                // Druid 监控页面相关路径
                                .requestMatchers(new AntPathRequestMatcher("/druid/**")).permitAll()
                                // 静态资源，可匿名访问
                                .requestMatchers(new AntPathRequestMatcher("/", HttpMethod.GET.name())).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/*.html", HttpMethod.GET.name())).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/**/*.html", HttpMethod.GET.name())).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/**/*.css", HttpMethod.GET.name())).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/**/*.js", HttpMethod.GET.name())).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/profile/**", HttpMethod.GET.name())).permitAll()
                                // 除上面外的所有请求全部需要鉴权认证
                                .anyRequest().authenticated();
                        log.info("Security configuration completed successfully");
                    } catch (Exception e) {
                        log.error("Error configuring security: ", e);
                        throw e;
                    }
                })
                // 添加Logout filter
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler))
                // 添加JWT filter
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 添加CORS filter
                .addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class)
                .addFilterBefore(corsFilter, LogoutFilter.class)
                .build();
    }

    /**
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
