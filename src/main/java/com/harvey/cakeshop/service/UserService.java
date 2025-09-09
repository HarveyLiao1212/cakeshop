package com.harvey.cakeshop.service;


import com.harvey.cakeshop.dto.UserRegisterRequest;
import com.harvey.cakeshop.model.User;
import com.harvey.cakeshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Integer register(UserRegisterRequest userRegisterRequest) {

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
