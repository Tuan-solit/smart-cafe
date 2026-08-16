package com.module3.ccafe.service;

import com.module3.ccafe.entity.Product;
import com.module3.ccafe.entity.enums.ProductStatus;
import com.module3.ccafe.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductService implements IProductService{
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAvailableProducts() {
        return productRepository.findByStatus(ProductStatus.AVAILABLE);
    }

    @Override
    public List<Product> findByCategory(Integer categoryId) {
        return productRepository.findByCategory_CategoryIdAndStatus(categoryId,ProductStatus.AVAILABLE);
    }

    @Override
    public List<Product> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseAndStatus(keyword,ProductStatus.AVAILABLE);
    }

    @Override
    public List<Product> searchByCategory(Integer categoryId, String keyword) {
        return productRepository.findByCategory_CategoryIdAndNameContainingIgnoreCaseAndStatus(categoryId,keyword,ProductStatus.AVAILABLE);
    }
}
