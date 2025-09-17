package com.harvey.cakeshop.dto;

import java.util.HashMap;
import java.util.Map;

public class UserLoginResponse {

    private Map<String, Object> info = new HashMap<>(); // 包 email 或其他個人資訊
    private String accessToken;
    private String refreshToken;

    public Map<String, Object> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
