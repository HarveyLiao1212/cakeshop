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

    @PostMapping("/users/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest){

        Integer userId = userService.register(userRegisterRequest);

        User user = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest userLoginRequest){

        UserLoginResponse userLoginResponse = userService.login(userLoginRequest);

        return ResponseEntity.status(HttpStatus.OK).body(userLoginResponse);

    }

    @PostMapping("/users/changepassword")
    public ResponseEntity<Map<String,String>> changepassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest){

        String accessToken = authHeader.replace("Bearer ", "");

        userService.changePassword(accessToken, changePasswordRequest);

        Map<String, String> response = new HashMap<>();
        response.put("message", "密碼修改成功");

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping("/users/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        String newAccessToken = userService.refreshAccessToken(request.getRefreshToken());

        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccessToken(newAccessToken);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



}
