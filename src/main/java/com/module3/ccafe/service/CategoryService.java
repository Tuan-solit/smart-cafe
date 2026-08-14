package com.module3.ccafe.service;

import com.module3.ccafe.dto.CategoryRequest;
import com.module3.ccafe.dto.CategoryResponse;
import com.module3.ccafe.entity.Category;
import com.module3.ccafe.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Integer categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy danh mục"));

        return toResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request) {

        String name = request.getName().trim();

        if (name.isBlank()) {
            throw new RuntimeException("Tên danh mục không được để trống");
        }

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }

        Category category = Category.builder()
                .name(name)
                .build();

        Category savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    public CategoryResponse updateCategory(
            Integer categoryId,
            CategoryRequest request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy danh mục"));

        String name = request.getName().trim();

        if (name.isBlank()) {
            throw new RuntimeException("Tên danh mục không được để trống");
        }

        if (categoryRepository.existsByNameIgnoreCaseAndCategoryIdNot(
                name,
                categoryId)) {

            throw new RuntimeException("Tên danh mục đã tồn tại");
        }

        category.setName(name);

        Category updatedCategory = categoryRepository.save(category);

        return toResponse(updatedCategory);
    }

    public void deleteCategory(Integer categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy danh mục"));

        if (category.getProducts() != null
                && !category.getProducts().isEmpty()) {

            throw new RuntimeException(
                    "Không thể xóa danh mục đang có sản phẩm"
            );
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category) {

        long productCount = category.getProducts() == null
                ? 0
                : category.getProducts().size();

        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .productCount(productCount)
                .build();
    }
}