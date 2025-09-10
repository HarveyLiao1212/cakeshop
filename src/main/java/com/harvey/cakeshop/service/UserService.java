package com.harvey.cakeshop.service;


import com.harvey.cakeshop.dto.ChangePasswordRequest;
import com.harvey.cakeshop.dto.UserLoginRequest;
import com.harvey.cakeshop.dto.UserLoginResponse;
import com.harvey.cakeshop.dto.UserRegisterRequest;
import com.harvey.cakeshop.model.User;
import com.harvey.cakeshop.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public Integer register(UserRegisterRequest userRegisterRequest) {

        // 檢查註冊過的Email
        Optional<User> email = userRepository.findByEmail(userRegisterRequest.getEmail());

        if (email.isPresent()) {
            log.warn("該Email {} 已經被註冊過",  userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "帳號已存在");
        }

        // 用sha256 生成密碼雜湊值
        String hashedPassword = DigestUtils.sha256Hex(userRegisterRequest.getPassword());


        // 創建帳號
        User user = new User();
        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        return user.getUserId();
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        Optional<User> userEmail = userRepository.findByEmail(userLoginRequest.getEmail());

        // 檢查user是否存在
        if (userEmail.isEmpty()) {
            log.warn("該Email {} 尚未註冊",  userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "帳號不存在");
        }

        // // 用sha256 生成密碼雜湊值
        String hashedPassword = DigestUtils.sha256Hex(userLoginRequest.getPassword());

        // 比較密碼
        if (userEmail.get().getPassword().equals(hashedPassword)) {

            // 產生 Access Token 和 Refresh Token
            String accessToken = jwtService.generateAccessToken(userLoginRequest.getEmail());
            String refreshToken = jwtService.generateRefreshToken(userLoginRequest.getEmail());

            // 組裝回傳物件
            UserLoginResponse userLoginResponse = new UserLoginResponse();
            userLoginResponse.setAccessToken(accessToken);
            userLoginResponse.setRefreshToken(refreshToken);
            userLoginResponse.setEmail(userEmail.get().getEmail());

            return  userLoginResponse;
        }else {
            log.warn("email {} 的密碼不正確",  userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "輸入密碼錯誤");
        }
    }

    public String refreshAccessToken(String refreshToken) {
        // 驗證 refresh token 是否有效
        if (!jwtService.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token 無效");
        }

        // 取出使用者 email
        String userEmail = jwtService.extractUsername(refreshToken);

        // 重新產生新的 access token
        return jwtService.generateAccessToken(userEmail);
    }

    public void changePassword(String accessToken, ChangePasswordRequest changePasswordRequest) {
        // 驗證 access token 是否有效
        if (!jwtService.validateToken(accessToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "access Token 無效");
        }
        // 取出使用者 email
        String userEmail = jwtService.extractUsername(accessToken);

        // 用sha256 生成新密碼雜湊值
        String newHashedPassword = DigestUtils.sha256Hex(changePasswordRequest.getNewPassword());

        String hashedPassword = DigestUtils.sha256Hex(changePasswordRequest.getOldPassword());

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        User user = optionalUser.get();
        if (user.getPassword().equals(hashedPassword)){
            user.setPassword(newHashedPassword);
            System.out.println("更改後的密碼雜湊值: " + user.getPassword());
            userRepository.save(user);
        }else {
            log.warn("舊密碼不正確");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "舊的密碼錯誤");
        }


    }



}
