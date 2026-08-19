package com.module3.ccafe.repository;

import com.module3.ccafe.entity.Product;
import com.module3.ccafe.entity.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByStatus(ProductStatus productStatus);

    List<Product> findByCategory_CategoryIdAndStatus(
            Integer categoryId,
            ProductStatus status
    );

    List<Product> findByNameContainingIgnoreCaseAndStatus(
            String name,
            ProductStatus status
    );

    List<Product> findByCategory_CategoryIdAndNameContainingIgnoreCaseAndStatus(
            Integer categoryId,
            String name,
            ProductStatus status
    );

    // ================================
    // ADMIN - SEARCH / FILTER
    // ================================

    @Query("""
            SELECT p
            FROM Product p
            WHERE (:keyword = ''
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:categoryId IS NULL
                   OR p.category.categoryId = :categoryId)
              AND (:status IS NULL
                   OR p.status = :status)
            """)
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("status") ProductStatus status,
            Pageable pageable
    );
}