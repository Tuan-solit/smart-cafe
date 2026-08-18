package com.module3.ccafe.service;

import com.module3.ccafe.entity.Product;

import java.util.List;

public interface IProductService {
    List<Product> findAvailableProducts();
    List<Product> findByCategory(Integer categoryId);
    List<Product> search(String keyword);
    List<Product> searchByCategory(Integer categoryId, String keyword);
}
