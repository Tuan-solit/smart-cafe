package com.module3.ccafe.repository;

import com.module3.ccafe.entity.Product;
import com.module3.ccafe.entity.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    List<Product> findByStatus(ProductStatus productStatus);
}
