package com.module3.ccafe.repository;

import com.module3.ccafe.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndCategoryIdNot(String name, Integer categoryId);
}
