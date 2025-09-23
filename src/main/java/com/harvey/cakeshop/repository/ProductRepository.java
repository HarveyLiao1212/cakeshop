package com.harvey.cakeshop.repository;

import com.harvey.cakeshop.constant.ProductCategory;
import com.harvey.cakeshop.model.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 查詢 分類 的所有商品
    List<Product> findByCategory(ProductCategory category, Sort sort);

    // 查詢 分類 且 商品名稱 包含關鍵字的商品
    List<Product> findByCategoryAndProductNameContaining(ProductCategory category, String keyword, Sort sort);

    // 查詢 商品名稱 包含關鍵字的所有商品
    List<Product> findByProductNameContaining(String keyword, Sort sort);



}
