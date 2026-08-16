package com.module3.ccafe.repository;

import com.module3.ccafe.entity.Product;
import com.module3.ccafe.entity.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByCategory_CategoryIdAndStatus(Integer categoryId, ProductStatus status);
    List<Product> findByNameContainingIgnoreCaseAndStatus(String name, ProductStatus status);
    List<Product> findByCategory_CategoryIdAndNameContainingIgnoreCaseAndStatus(Integer categoryId, String name, ProductStatus status);
    
}
