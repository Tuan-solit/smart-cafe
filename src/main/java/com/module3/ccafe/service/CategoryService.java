package com.module3.ccafe.service;

import com.module3.ccafe.dto.CategoryRequest;
import com.module3.ccafe.dto.CategoryResponse;
import com.module3.ccafe.entity.Category;
import com.module3.ccafe.entity.enums.CategoryStatus;
import com.module3.ccafe.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;


    // =========================================================
    // FIND ALL
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {

        return categoryRepository.findAll();
    }


    // =========================================================
    // GET ALL ACTIVE CATEGORIES
    // =========================================================

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository
                .findAll()
                .stream()
                .filter(category ->
                        category.getStatus() == CategoryStatus.ACTIVE)
                .map(this::toResponse)
                .toList();
    }


    // =========================================================
    // GET CATEGORY BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Integer categoryId) {

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy danh mục"));

        if (category.getStatus() != CategoryStatus.ACTIVE) {

            throw new RuntimeException(
                    "Danh mục không còn hoạt động");
        }

        return toResponse(category);
    }


    // =========================================================
    // SEARCH + PAGINATION
    // =========================================================

    @Transactional(readOnly = true)
    public Page<CategoryResponse> searchCategories(
            String keyword,
            Pageable pageable) {

        return categoryRepository
                .findByNameContainingIgnoreCaseAndStatus(
                        keyword,
                        CategoryStatus.ACTIVE,
                        pageable
                )
                .map(this::toResponse);
    }


    // =========================================================
    // CREATE CATEGORY
    // =========================================================

    public CategoryResponse createCategory(
            CategoryRequest request) {

        String name = request.getName().trim();

        if (name.isBlank()) {

            throw new RuntimeException(
                    "Tên danh mục không được để trống");
        }

        if (categoryRepository.existsByNameIgnoreCase(name)) {

            throw new RuntimeException(
                    "Tên danh mục đã tồn tại");
        }

        Category category = Category.builder()
                .name(name)
                .status(CategoryStatus.ACTIVE)
                .build();

        Category savedCategory =
                categoryRepository.save(category);

        return toResponse(savedCategory);
    }


    // =========================================================
    // UPDATE CATEGORY
    // =========================================================

    public CategoryResponse updateCategory(
            Integer categoryId,
            CategoryRequest request) {

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy danh mục"));

        if (category.getStatus() != CategoryStatus.ACTIVE) {

            throw new RuntimeException(
                    "Danh mục không còn hoạt động");
        }

        String name = request.getName().trim();

        if (name.isBlank()) {

            throw new RuntimeException(
                    "Tên danh mục không được để trống");
        }

        if (categoryRepository
                .existsByNameIgnoreCaseAndCategoryIdNot(
                        name,
                        categoryId)) {

            throw new RuntimeException(
                    "Tên danh mục đã tồn tại");
        }

        category.setName(name);

        Category updatedCategory =
                categoryRepository.save(category);

        return toResponse(updatedCategory);
    }


    // =========================================================
    // SOFT DELETE CATEGORY
    // =========================================================

    public void deleteCategory(Integer categoryId) {

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy danh mục"));

        if (category.getStatus() != CategoryStatus.ACTIVE) {

            throw new RuntimeException(
                    "Danh mục đã được ngừng sử dụng");
        }

        /*
         * XÓA MỀM:
         *
         * Không sử dụng:
         *
         * categoryRepository.delete(category);
         *
         * Vì cách đó sẽ xóa record khỏi database.
         */

        category.setStatus(CategoryStatus.INACTIVE);

        categoryRepository.save(category);
    }


    // =========================================================
    // CONVERT ENTITY -> RESPONSE
    // =========================================================

    private CategoryResponse toResponse(
            Category category) {

        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .status(category.getStatus())
                .build();
    }
}