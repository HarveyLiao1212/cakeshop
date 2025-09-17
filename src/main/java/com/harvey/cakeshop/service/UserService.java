package com.harvey.cakeshop.service;


import com.harvey.cakeshop.dto.account.ChangePasswordRequest;
import com.harvey.cakeshop.dto.account.UserLoginRequest;
import com.harvey.cakeshop.dto.account.UserLoginResponse;
import com.harvey.cakeshop.dto.account.UserRegisterRequest;
import com.harvey.cakeshop.model.User;
import com.harvey.cakeshop.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    // 1.註冊功能
    public User register(UserRegisterRequest userRegisterRequest) {

        // 檢查註冊過的Email
        Optional<User> optionalUser = userRepository.findByEmail(userRegisterRequest.getEmail());

        if (optionalUser.isPresent()) {
            log.warn("該Email {} 已經被註冊過",  userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "帳號已存在");
        }

        // 用 sha256 生成密碼雜湊值
        String hashedPassword = DigestUtils.sha256Hex(userRegisterRequest.getPassword());

        // 創建帳號
        User user = new User();
        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // 回傳使用者資料
        return user;
    }

    // 2.登入功能
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        // 檢查帳戶是否存在
        Optional<User> optionalUser = userRepository.findByEmail(userLoginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            log.warn("該Email {} 尚未註冊",  userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "帳號不存在");
        }

        // 用sha256 生成密碼雜湊值
        String hashedPassword = DigestUtils.sha256Hex(userLoginRequest.getPassword());

        // 比較密碼
        if (optionalUser.get().getPassword().equals(hashedPassword)) {

            // 產生 Access Token 和 Refresh Token
            String accessToken = jwtService.generateAccessToken(userLoginRequest.getEmail());
            String refreshToken = jwtService.generateRefreshToken(userLoginRequest.getEmail());

            // 組裝回傳物件(email 和 Access Token 和 Refresh Token)
            UserLoginResponse userLoginResponse = new UserLoginResponse();

            userLoginResponse.setAccessToken(accessToken);
            userLoginResponse.setRefreshToken(refreshToken);

            Map<String, Object> info = new HashMap<>();
            info.put("email", optionalUser.get().getEmail());
            userLoginResponse.setInfo(info);

            return  userLoginResponse;

        }else {
            log.warn("email {} 的密碼不正確",  userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "輸入密碼錯誤");
        }
    }

    // 3.修改密碼
    public void changePassword(String userEmail, ChangePasswordRequest changePasswordRequest) {

        // 用 sha256 生成新密碼雜湊值
        String newHashedPassword = DigestUtils.sha256Hex(changePasswordRequest.getNewPassword());
        // 舊密碼雜湊值
        String hashedPassword = DigestUtils.sha256Hex(changePasswordRequest.getOldPassword());

        // 資料庫查詢email
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        // 取出 optional 裡的物件
        User user = optionalUser.get();

        // 驗證舊密碼是否正確
        if (user.getPassword().equals(hashedPassword)){
            user.setPassword(newHashedPassword);
            userRepository.save(user);
            System.out.println("更改後的密碼雜湊值: " + user.getPassword());
        }else {
            log.warn("舊密碼不正確");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "舊的密碼錯誤");
        }
    }

    // 4.更新token
    public String refreshAccessToken(String userEmail) {

        // 重新產生新的 access token
        return jwtService.generateAccessToken(userEmail);
    }



}
