package com.module3.ccafe.service;

import com.module3.ccafe.dto.ProductRequest;
import com.module3.ccafe.dto.ProductResponse;
import com.module3.ccafe.entity.Category;
import com.module3.ccafe.entity.Product;
import com.module3.ccafe.entity.Size;
import com.module3.ccafe.entity.enums.ProductStatus;
import com.module3.ccafe.repository.CategoryRepository;
import com.module3.ccafe.repository.ProductRepository;
import com.module3.ccafe.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;


    // =========================================================
    // CUSTOMER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAvailableProducts() {

        return productRepository.findByStatus(
                ProductStatus.AVAILABLE
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<Product> findByCategory(Integer categoryId) {

        return productRepository
                .findByCategory_CategoryIdAndStatus(
                        categoryId,
                        ProductStatus.AVAILABLE
                );
    }


    @Override
    @Transactional(readOnly = true)
    public List<Product> search(String keyword) {

        return productRepository
                .findByNameContainingIgnoreCaseAndStatus(
                        keyword,
                        ProductStatus.AVAILABLE
                );
    }


    @Override
    @Transactional(readOnly = true)
    public List<Product> searchByCategory(
            Integer categoryId,
            String keyword) {

        return productRepository
                .findByCategory_CategoryIdAndNameContainingIgnoreCaseAndStatus(
                        categoryId,
                        keyword,
                        ProductStatus.AVAILABLE
                );
    }


    // =========================================================
    // ADMIN - PRODUCT LIST
    // =========================================================

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer productId) {

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy sản phẩm"
                        )
                );

        return toResponse(product);
    }


    // =========================================================
    // ADMIN - CREATE
    // =========================================================

    public ProductResponse createProduct(
            ProductRequest request,
            String imageUrl) {

        // Kiểm tra dữ liệu cơ bản trước
        validateBasicProductRequest(request);


        // -----------------------------------------------------
        // CATEGORY
        // -----------------------------------------------------

        Category category = null;

        if (request.getCategoryId() != null) {

            category = categoryRepository
                    .findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Không tìm thấy danh mục"
                            )
                    );
        }


        // -----------------------------------------------------
        // SIZE
        // -----------------------------------------------------

        Size size = resolveSize(
                category,
                request.getSizeId()
        );


        // -----------------------------------------------------
        // CREATE PRODUCT
        // -----------------------------------------------------

        Product product = Product.builder()
                .name(request.getName().trim())
                .category(category)
                .size(size)
                .price(request.getPrice())
                .image(imageUrl)
                .status(request.getStatus())
                .build();


        Product savedProduct =
                productRepository.save(product);

        return toResponse(savedProduct);
    }


    // =========================================================
    // ADMIN - UPDATE
    // =========================================================

    public ProductResponse updateProduct(
            Integer productId,
            ProductRequest request,
            String imageUrl) {

        validateBasicProductRequest(request);


        // -----------------------------------------------------
        // FIND PRODUCT
        // -----------------------------------------------------

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy sản phẩm"
                        )
                );


        // -----------------------------------------------------
        // DISCONTINUED
        // -----------------------------------------------------

        if (product.getStatus() == ProductStatus.DISCONTINUED) {

            throw new RuntimeException(
                    "Sản phẩm đã ngừng bán vĩnh viễn"
            );
        }


        // -----------------------------------------------------
        // CATEGORY
        // -----------------------------------------------------

        Category category = null;

        if (request.getCategoryId() != null) {

            category = categoryRepository
                    .findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Không tìm thấy danh mục"
                            )
                    );
        }


        // -----------------------------------------------------
        // SIZE
        // -----------------------------------------------------

        Size size = resolveSize(
                category,
                request.getSizeId()
        );


        // -----------------------------------------------------
        // UPDATE
        // -----------------------------------------------------

        product.setName(
                request.getName().trim()
        );

        product.setCategory(category);

        product.setSize(size);

        product.setPrice(
                request.getPrice()
        );

        product.setStatus(
                request.getStatus()
        );


        // Chỉ cập nhật ảnh nếu user chọn ảnh mới
        if (imageUrl != null
                && !imageUrl.isBlank()) {

            product.setImage(imageUrl);
        }


        Product updatedProduct =
                productRepository.save(product);

        return toResponse(updatedProduct);
    }


    // =========================================================
    // ADMIN - SOFT DELETE
    // =========================================================

    public void deleteProduct(Integer productId) {

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy sản phẩm"
                        )
                );


        if (product.getStatus()
                == ProductStatus.DISCONTINUED) {

            throw new RuntimeException(
                    "Sản phẩm này đã ngừng bán"
            );
        }


        /*
         * SOFT DELETE
         *
         * Không xóa record khỏi database.
         * Chỉ chuyển status thành DISCONTINUED.
         */
        product.setStatus(
                ProductStatus.DISCONTINUED
        );

        productRepository.save(product);
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    /**
     * Kiểm tra các thông tin cơ bản của sản phẩm.
     *
     * Không kiểm tra size ở đây vì việc size có bắt buộc
     * hay không phụ thuộc vào category.
     */
    private void validateBasicProductRequest(
            ProductRequest request) {

        if (request == null) {

            throw new RuntimeException(
                    "Dữ liệu sản phẩm không hợp lệ"
            );
        }


        // -----------------------------------------------------
        // NAME
        // -----------------------------------------------------

        if (request.getName() == null
                || request.getName().trim().isBlank()) {

            throw new RuntimeException(
                    "Tên sản phẩm không được để trống"
            );
        }


        // -----------------------------------------------------
        // PRICE
        // -----------------------------------------------------

        if (request.getPrice() == null
                || request.getPrice().signum() <= 0) {

            throw new RuntimeException(
                    "Giá sản phẩm phải lớn hơn 0"
            );
        }


        // -----------------------------------------------------
        // CATEGORY
        // -----------------------------------------------------

        if (request.getCategoryId() == null) {

            throw new RuntimeException(
                    "Vui lòng chọn danh mục"
            );
        }


        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        if (request.getStatus() == null) {

            throw new RuntimeException(
                    "Vui lòng chọn trạng thái"
            );
        }
    }


    // =========================================================
    // RESOLVE SIZE
    // =========================================================

    /**
     * Xác định Size dựa vào Category.
     *
     * Nếu Category là Cafe:
     *      -> Size = NULL
     *
     * Nếu Category khác Cafe:
     *      -> Bắt buộc phải chọn Size
     */
    private Size resolveSize(
            Category category,
            Integer sizeId) {

        if (category == null) {

            throw new RuntimeException(
                    "Vui lòng chọn danh mục"
            );
        }


        // -----------------------------------------------------
        // CAFE
        // -----------------------------------------------------

        if (isCafeCategory(category)) {

            /*
             * Cafe không sử dụng Size.
             *
             * Dù frontend có gửi sizeId thì backend
             * vẫn bỏ qua và lưu NULL.
             */
            return null;
        }

        if (isCakeCategory(category)) {
            return null;
        }


        // -----------------------------------------------------
        // CATEGORY KHÁC CAFE
        // -----------------------------------------------------

        if (sizeId == null) {

            throw new RuntimeException(
                    "Vui lòng chọn kích thước"
            );
        }


        return sizeRepository
                .findById(sizeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy kích thước"
                        )
                );
    }


    // =========================================================
    // CHECK CAFE CATEGORY
    // =========================================================

    /**
     * Kiểm tra category có phải Cafe hay không.
     *
     * IgnoreCase để:
     * Cafe
     * cafe
     * CAFE
     *
     * đều được xem là Cafe.
     */
    private boolean isCafeCategory(
            Category category) {

        return category.getName() != null
                && category.getName()
                .trim()
                .equalsIgnoreCase("Cafe");
    }

    private boolean isCakeCategory(Category category) {
        return category.getName() != null && category.getName().trim().equalsIgnoreCase("Cake");
    }


    // =========================================================
    // ADMIN - SEARCH
    // =========================================================

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(
            String keyword,
            Integer categoryId,
            ProductStatus status,
            Pageable pageable) {

        return productRepository
                .searchProducts(
                        keyword == null
                                ? ""
                                : keyword.trim(),
                        categoryId,
                        status,
                        pageable
                )
                .map(this::toResponse);
    }


    // =========================================================
    // RESPONSE
    // =========================================================

    private ProductResponse toResponse(
            Product product) {

        return ProductResponse.builder()

                .productId(
                        product.getProductId()
                )

                .name(
                        product.getName()
                )

                .price(
                        product.getPrice()
                )

                .image(
                        product.getImage()
                )

                .categoryId(
                        product.getCategory() != null
                                ? product.getCategory()
                                  .getCategoryId()
                                : null
                )

                .categoryName(
                        product.getCategory() != null
                                ? product.getCategory()
                                  .getName()
                                : null
                )

                .sizeId(
                        product.getSize() != null
                                ? product.getSize()
                                  .getSizeId()
                                : null
                )

                .sizeName(
                        product.getSize() != null
                                ? product.getSize()
                                  .getName()
                                : null
                )

                .status(
                        product.getStatus()
                )

                .statusDescription(
                        product.getStatus() != null
                                ? product.getStatus()
                                  .getDescription()
                                : null
                )

                .build();
    }
}