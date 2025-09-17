package com.harvey.cakeshop.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 關閉 CSRF
        http.csrf(csrf -> csrf.disable());

        // 設定授權規則
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/login", "/users/register", "/products/{productId}").permitAll() // 登入/註冊放行
                .anyRequest().authenticated() // 其他 API 需要驗證
        );

        // 加入 JWT Filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}




