package com.harvey.cakeshop.service;


import com.harvey.cakeshop.dto.UserRegisterRequest;
import com.harvey.cakeshop.model.User;
import com.harvey.cakeshop.repository.UserRepository;
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

    public Integer register(UserRegisterRequest userRegisterRequest) {

        // 檢查註冊過的Email
        Optional<User> email = userRepository.findByEmail(userRegisterRequest.getEmail());

        if (email.isPresent()) {
            log.warn("該Email {} 已經被註冊過",  userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 創建帳號
        User user = new User();
        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(userRegisterRequest.getPassword());
        userRepository.save(user);

        return user.getUserId();
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
