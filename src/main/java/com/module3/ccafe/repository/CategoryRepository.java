package com.module3.ccafe.repository;

import com.module3.ccafe.entity.Category;
import com.module3.ccafe.entity.enums.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndCategoryIdNot(
            String name,
            Integer categoryId
    );

    Page<Category> findByNameContainingIgnoreCaseAndStatus(
            String keyword,
            CategoryStatus status,
            Pageable pageable
    );
}