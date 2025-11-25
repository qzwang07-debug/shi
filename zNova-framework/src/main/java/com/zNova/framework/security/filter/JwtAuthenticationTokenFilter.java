package com.zNova.framework.security.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.zNova.common.core.domain.model.LoginUser;
import com.zNova.common.core.domain.model.AppLoginUser;
import com.zNova.common.utils.SecurityUtils;
import com.zNova.common.utils.StringUtils;
import com.zNova.framework.web.service.TokenService;
import com.zNova.framework.web.service.AppTokenService;

/**
 * token过滤器 验证token有效性
 * * @author ruoyi
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter
{
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AppTokenService appTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        String requestUri = request.getRequestURI();
        log.debug("收到请求: {}", requestUri);

        // 1. 尝试获取后台用户 LoginUser
        LoginUser loginUser = tokenService.getLoginUser(request);
        log.debug("尝试获取后台用户: {}", loginUser);
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication()))
        {
            tokenService.verifyToken(loginUser);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.debug("后台用户认证成功: {}", loginUser.getUsername());
        }
        // 2. 如果没有获取到后台用户，尝试获取 C端用户 AppLoginUser
        else
        {
            AppLoginUser appLoginUser = appTokenService.getLoginUser(request);
            log.debug("尝试获取C端用户: {}", appLoginUser);
            if (StringUtils.isNotNull(appLoginUser) && StringUtils.isNull(SecurityUtils.getAuthentication()))
            {
                appTokenService.verifyToken(appLoginUser);
                // 将 AppLoginUser 放入 SecurityContext
                // 注意：AppLoginUser 需要有 getAuthorities() 方法，或者传空集合
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(appLoginUser, null, appLoginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("C端用户认证成功: userId={}, username={}", appLoginUser.getUserId(), appLoginUser.getUsername());
                log.debug("SecurityContext authentication: {}", SecurityContextHolder.getContext().getAuthentication());
            }
        }

        log.debug("SecurityContext authentication: {}", SecurityUtils.getAuthentication());

        chain.doFilter(request, response);
    }
}