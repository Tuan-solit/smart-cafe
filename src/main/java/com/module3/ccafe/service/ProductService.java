package com.module3.ccafe.service;

import com.module3.ccafe.dto.ProductRequest;
import com.module3.ccafe.dto.ProductResponse;
import com.module3.ccafe.entity.Category;
import com.module3.ccafe.entity.Product;
import com.module3.ccafe.entity.Size;
import com.module3.ccafe.repository.CategoryRepository;
import com.module3.ccafe.repository.ProductRepository;
import com.module3.ccafe.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy sản phẩm"));

        return toResponse(product);
    }

    public ProductResponse createProduct(ProductRequest request, String imageUrl) {

        if (request.getName() == null
                || request.getName().trim().isBlank()) {

            throw new RuntimeException(
                    "Tên sản phẩm không được để trống"
            );
        }

        if (request.getPrice() == null
                || request.getPrice().signum() <= 0) {

            throw new RuntimeException(
                    "Giá sản phẩm phải lớn hơn 0"
            );
        }

        if (request.getCategoryId() == null) {

            throw new RuntimeException(
                    "Vui lòng chọn danh mục"
            );
        }

        if (request.getSizeId() == null) {

            throw new RuntimeException(
                    "Vui lòng chọn kích thước"
            );
        }

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy danh mục"
                        ));

        Size size = sizeRepository
                .findById(request.getSizeId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy kích thước"
                        ));

        Product product = Product.builder()
                .name(request.getName().trim())
                .category(category)
                .size(size)
                .price(request.getPrice())
                .image(imageUrl)
                .status(request.getStatus())
                .build();

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    public ProductResponse updateProduct(
            Integer productId,
            ProductRequest request,
            String imageUrl) {

        validateProductRequest(request);

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy sản phẩm"
                        ));

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy danh mục"
                        ));

        Size size = sizeRepository
                .findById(request.getSizeId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy kích thước"
                        ));

        product.setName(request.getName().trim());
        product.setCategory(category);
        product.setSize(size);
        product.setPrice(request.getPrice());
        product.setStatus(request.getStatus());

        /*
         * Nếu có ảnh mới thì cập nhật ảnh mới.
         * Nếu không có ảnh mới thì giữ nguyên ảnh cũ.
         */
        if (imageUrl != null && !imageUrl.isBlank()) {
            product.setImage(imageUrl);
        }

        Product updatedProduct =
                productRepository.save(product);

        return toResponse(updatedProduct);
    }

    public void deleteProduct(Integer productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy sản phẩm"
                        ));

        if (product.getOrderDetails() != null
                && !product.getOrderDetails().isEmpty()) {

            throw new RuntimeException(
                    "Không thể xóa sản phẩm đã có trong đơn hàng"
            );
        }

        productRepository.delete(product);
    }

    // ================================
    // VALIDATION
    // ================================

    private void validateProductRequest(
            ProductRequest request) {

        if (request.getName() == null
                || request.getName().trim().isBlank()) {

            throw new RuntimeException(
                    "Tên sản phẩm không được để trống"
            );
        }

        if (request.getPrice() == null
                || request.getPrice().signum() <= 0) {

            throw new RuntimeException(
                    "Giá sản phẩm phải lớn hơn 0"
            );
        }

        if (request.getCategoryId() == null) {

            throw new RuntimeException(
                    "Vui lòng chọn danh mục"
            );
        }

        if (request.getSizeId() == null) {

            throw new RuntimeException(
                    "Vui lòng chọn kích thước"
            );
        }

        if (request.getStatus() == null) {

            throw new RuntimeException(
                    "Vui lòng chọn trạng thái"
            );
        }
    }

    private ProductResponse toResponse(Product product) {

        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .image(product.getImage())
                .categoryId(
                        product.getCategory() != null
                                ? product.getCategory().getCategoryId()
                                : null
                )
                .categoryName(
                        product.getCategory() != null
                                ? product.getCategory().getName()
                                : null
                )
                .sizeId(
                        product.getSize() != null
                                ? product.getSize().getSizeId()
                                : null
                )
                .sizeName(
                        product.getSize() != null
                                ? product.getSize().getName()
                                : null
                )
                .status(product.getStatus())
                .statusDescription(
                        product.getStatus() != null
                                ? product.getStatus().getDescription()
                                : null
                )
                .build();
    }
}