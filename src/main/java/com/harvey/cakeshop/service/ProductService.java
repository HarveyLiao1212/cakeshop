package com.harvey.cakeshop.service;


import com.harvey.cakeshop.dto.product.ProductCreateRequest;
import com.harvey.cakeshop.model.Product;
import com.harvey.cakeshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product getProductById(Integer productId) {

        return productRepository.findById(productId).orElse(null);

    }

    public Integer createProduct(ProductCreateRequest productCreateRequest) {

        Product product = new Product();
        product.setProductName(productCreateRequest.getProductName());
        product.setCategory(productCreateRequest.getCategory());
        product.setImageUrl(productCreateRequest.getImageUrl());
        product.setPrice(productCreateRequest.getPrice());
        product.setStock(productCreateRequest.getStock());
        product.setDescription(productCreateRequest.getDescription());
        productRepository.save(product);

        return product.getProductId();
    }


}
