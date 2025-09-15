package com.harvey.cakeshop.controller;

import com.harvey.cakeshop.dto.*;
import com.harvey.cakeshop.model.User;
import com.harvey.cakeshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // 註冊
    @PostMapping("/users/register")
    public ResponseEntity<UserRegisterResponse> register(
            @RequestBody @Valid UserRegisterRequest userRegisterRequest){

        // 資料傳入 userService 裡的 register 去做註冊功能並取回資料
        User user = userService.register(userRegisterRequest);

        // 註冊成功組裝回傳使用者資料
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse();
        userRegisterResponse.setUserId(user.getUserId());
        userRegisterResponse.setEmail(user.getEmail());
        userRegisterResponse.setCreateDate(user.getCreateDate());
        userRegisterResponse.setLastModifiedDate(user.getLastModifiedDate());

        return ResponseEntity.status(HttpStatus.CREATED).body(userRegisterResponse);
    }

    // 登入
    @PostMapping("/users/login")
    public ResponseEntity<UserLoginResponse> login(
            @RequestBody @Valid UserLoginRequest userLoginRequest){

        // 資料傳入 userService 裡的 login 去做登入驗證並取回資料
        UserLoginResponse userLoginResponse = userService.login(userLoginRequest);

        return ResponseEntity.status(HttpStatus.OK).body(userLoginResponse);

    }

    // 修改密碼
    @PostMapping("/users/changepassword")
    public ResponseEntity<Map<String,String>> changepassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest){

        // 標準規範 讓前後端統一知道這是 JWT token
        String accessToken = authHeader.replace("Bearer ", "");

        // 傳入userService裡的changePassword去做驗證及修改密碼
        userService.changePassword(accessToken, changePasswordRequest);

        // 修改成功
        Map<String, String> response = new HashMap<>(); // 用來存放等等傳入body的訊息
        response.put("message", "密碼修改成功"); // spring boot 會自動把 map 轉 json 傳回body

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    // 更新token
    @PostMapping("/users/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @RequestHeader("Authorization") String authHeader) {

        // 標準規範 讓前後端統一知道這是 JWT token
        String refreshToken = authHeader.replace("Bearer ", "");

        // 傳入 Refresh Token 到 userService 裡的refreshAccessToken去申請新的 Access Token
        String newAccessToken = userService.refreshAccessToken(refreshToken);

        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccessToken(newAccessToken);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



}
