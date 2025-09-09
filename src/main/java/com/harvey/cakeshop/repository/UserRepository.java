package com.harvey.cakeshop.repository;


import com.harvey.cakeshop.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
