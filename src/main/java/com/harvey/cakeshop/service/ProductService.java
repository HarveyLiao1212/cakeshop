package com.harvey.cakeshop.service;


import com.harvey.cakeshop.model.Product;
import com.harvey.cakeshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product getProductById(Integer productId) {

        Optional<Product> optional = productRepository.findById(productId);

        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }


    }


}
