package com.harvey.cakeshop.service;


import com.harvey.cakeshop.dto.product.ProductCreateRequest;
import com.harvey.cakeshop.model.Product;
import com.harvey.cakeshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // 查詢商品
    public Product getProductById(Integer productId) {

        return productRepository.findById(productId).orElse(null);
    }

    // 新增商品
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

    // 更新商品
    public Product updateProduct(Integer productId,ProductCreateRequest productCreateRequest) {

        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在，無法更新");
        }

        Product product = optionalProduct.get();
        product.setProductName(productCreateRequest.getProductName());
        product.setCategory(productCreateRequest.getCategory());
        product.setImageUrl(productCreateRequest.getImageUrl());
        product.setPrice(productCreateRequest.getPrice());
        product.setStock(productCreateRequest.getStock());
        product.setDescription(productCreateRequest.getDescription());
        productRepository.save(product);

        return product;
    }

    // 刪除商品
    public void deleteProduct(Integer productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到商品，無法刪除");
        }

        productRepository.deleteById(productId);

    }

}
