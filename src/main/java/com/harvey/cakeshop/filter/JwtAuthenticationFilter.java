package com.harvey.cakeshop.filter;

import com.harvey.cakeshop.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // 標準規範 讓前後端統一知道這是 JWT token
            String accessToken = authHeader.replace("Bearer ", "");

            // 到 jwtService 的 validateToken 驗證 access token 是否有效
            if (jwtService.validateToken(accessToken) == false ) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"message\": \"access Token 無效\"}");
                return; // 停止往下傳
            }

            // 驗證通過到 jwtService 的 extractUsername 取出使用者 email
            String userEmail = jwtService.extractUsername(accessToken);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userEmail, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        // 傳入下一層
        filterChain.doFilter(request, response);
    }

}

