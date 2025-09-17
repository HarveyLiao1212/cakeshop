package com.harvey.cakeshop.controller;

import com.harvey.cakeshop.constant.ProductCategory;
import com.harvey.cakeshop.dto.product.ProductCreateRequest;
import com.harvey.cakeshop.model.Product;
import com.harvey.cakeshop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    // 查詢所有商品
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String search) {

        List<Product> productList = productService.getProducts(category,search);

        if (productList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "沒有任何商品");
        }

        return ResponseEntity.status(HttpStatus.OK).body(productList);
    }

    // 查詢指定商品
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProduct(
            @PathVariable Integer productId) {

        Product product = productService.getProductById(productId);

        if (product == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品不存在，無法查詢");
        }  else {
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }
    }

    // 新增商品
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(
            @RequestBody @Valid ProductCreateRequest productCreateRequest) {

        Integer productId = productService.createProduct(productCreateRequest);
        Product product = productService.getProductById(productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    // 修改商品
    @PostMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Integer productId,
            @RequestBody @Valid ProductCreateRequest productCreateRequest) {

        // 修改商品數據
        Product updatedProduct = productService.updateProduct(productId,productCreateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    // 刪除商品
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Integer productId) {

        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
