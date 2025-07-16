package com.xinletu.framework.security.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.xinletu.common.core.domain.model.LoginUser;
import com.xinletu.common.utils.SecurityUtils;
import com.xinletu.common.utils.StringUtils;
import com.xinletu.framework.web.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * token过滤器 验证token有效性
 * 
 * @author ruoyi
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter
{
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    
    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        // 记录请求信息
        String requestURI = request.getRequestURI();
        log.info("Processing request: {}", requestURI);
        
        try {
            LoginUser loginUser = tokenService.getLoginUser(request);
            if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication()))
            {
                tokenService.verifyToken(loginUser);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("Set Authentication for user: {}", loginUser.getUsername());
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error processing request: {}", requestURI, e);
            throw e;
        }
    }
}
