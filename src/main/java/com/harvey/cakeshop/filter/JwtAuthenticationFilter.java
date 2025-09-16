package com.harvey.cakeshop.filter;

import com.harvey.cakeshop.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

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

        // 取得本次請求的 URI，用來判斷是否需要進行 JWT 驗證
        String path = request.getRequestURI();

        // 如果是登入或註冊的請求，直接放行，不做 JWT 驗證
        // 因為這些 API 還沒有 token，所以不能攔截
        if (path.equals("/users/login") || path.equals("/users/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // 標準規範 讓前後端統一知道這是 JWT token
            String accessToken = authHeader.replace("Bearer ", "");

            // 到 jwtService 的 validateToken 驗證 access token 是否有效
            if (jwtService.validateToken(accessToken) == false ) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "access Token 無效");
            }

            // 驗證通過到 jwtService 的 extractUsername 取出使用者 email
            String userEmail = jwtService.extractUsername(accessToken);

            // 存進 request attribute (給 controller 用)
            request.setAttribute("email", userEmail);
        }

        // 傳入下一層
        filterChain.doFilter(request, response);
    }

}

