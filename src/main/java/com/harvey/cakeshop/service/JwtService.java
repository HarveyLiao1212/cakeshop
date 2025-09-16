package com.harvey.cakeshop.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // 設定 secret key
    private final String secret = "ThisIsASimpleSecretKeyForJwt1234567890";

    // 字串 → 位元陣列 → Key 物件
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    // 1. 產生 Access Token(header + payload + signature)
    public String generateAccessToken(String Email) {

        long accessTokenExpiration = 1000 * 60 * 60; // 1 小時

        return Jwts.builder() // JWT 建構器
                .setSubject(Email)
                .setIssuedAt(new Date()) // 設定 token 發行時間
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration)) // 設定過期時間(現在時間+過期時間)
                .signWith(key, SignatureAlgorithm.HS256) // 設定 key 和演算法
                .compact(); // 把 JWT 壓縮成字串成為 token
    }

    // 2. 產生 Refresh Token
    public String generateRefreshToken(String Email) {

        long refreshTokenExpiration = 1000 * 60 * 60 * 24; // 1 天

        return Jwts.builder()
                .setSubject(Email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. 取得使用者email
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody() // 取得 payload
                .getSubject(); // 取得 email
    }

    // 4. 驗證 Token 是否有效
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder() // 建立一個 JWT 解析器
                    .setSigningKey(key) // 設定密鑰來解析
                    .build() // 設定完成
                    .parseClaimsJws(token); // 開始解析token內容
            // 驗證成功
            return true;
        } catch (JwtException e) {
            // 所有 JWT 相關的錯誤
            return false;
        } catch (IllegalArgumentException e) {
            // token 為 null、空字串或其他非法參數
            return false;
        }
    }
}

