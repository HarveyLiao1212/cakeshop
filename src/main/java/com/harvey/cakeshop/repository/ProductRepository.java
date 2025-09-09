package com.harvey.cakeshop.repository;

import com.harvey.cakeshop.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {


}
