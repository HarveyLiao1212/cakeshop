package com.harvey.cakeshop.controller;

import com.harvey.cakeshop.dto.product.ProductCreateRequest;
import com.harvey.cakeshop.model.Product;
import com.harvey.cakeshop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{productId}") // 查詢商品
    public ResponseEntity<Product> getProduct(@PathVariable Integer productId) {

        Product product = productService.getProductById(productId);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }  else {
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(
            @RequestBody @Valid ProductCreateRequest productCreateRequest) {

        Integer productId = productService.createProduct(productCreateRequest);

        Product product = productService.getProductById(productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

}
