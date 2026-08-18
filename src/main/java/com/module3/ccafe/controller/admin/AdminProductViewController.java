package com.module3.ccafe.controller.admin;

import com.module3.ccafe.dto.ProductRequest;
import com.module3.ccafe.dto.ProductResponse;
import com.module3.ccafe.entity.enums.ProductStatus;
import com.module3.ccafe.service.CategoryService;
import com.module3.ccafe.service.CloudinaryService;
import com.module3.ccafe.service.ProductService;
import com.module3.ccafe.service.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class AdminProductViewController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SizeService sizeService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("/page")
    public String productPage(Model model) {

        model.addAttribute(
                "productList",
                productService.getAllProducts()
        );

        return "admin/product/list";
    }


    // ================================
    // CREATE PRODUCT
    // ================================

    @GetMapping("/create")
    public String createPage(Model model) {

        model.addAttribute(
                "productRequest",
                new ProductRequest()
        );

        loadFormData(model);

        return "admin/product/create";
    }


    @PostMapping("/create")
    public String createProduct(
            @ModelAttribute("productRequest") ProductRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            String imageUrl = null;

            if (request.getImage() != null
                    && !request.getImage().isEmpty()) {

                imageUrl =
                        cloudinaryService.uploadImage(
                                request.getImage()
                        );
            }

            productService.createProduct(
                    request,
                    imageUrl
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Thêm sản phẩm thành công!"
            );

            return "redirect:/admin/product/page";

        } catch (RuntimeException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            loadFormData(model);

            return "admin/product/create";
        }
    }


    // ================================
    // EDIT PRODUCT
    // ================================

    @GetMapping("/edit/{productId}")
    public String editPage(
            @PathVariable Integer productId,
            Model model) {

        ProductResponse product =
                productService.getProductById(productId);

        ProductRequest request = ProductRequest.builder()
                .name(product.getName())
                .categoryId(product.getCategoryId())
                .sizeId(product.getSizeId())
                .price(product.getPrice())
                .status(product.getStatus())
                .build();

        model.addAttribute(
                "productRequest",
                request
        );

        model.addAttribute(
                "product",
                product
        );

        loadFormData(model);

        return "admin/product/edit";
    }


    @PostMapping("/edit/{productId}")
    public String updateProduct(
            @PathVariable Integer productId,
            @ModelAttribute("productRequest") ProductRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            String imageUrl = null;

            if (request.getImage() != null
                    && !request.getImage().isEmpty()) {

                imageUrl =
                        cloudinaryService.uploadImage(
                                request.getImage()
                        );
            }

            productService.updateProduct(
                    productId,
                    request,
                    imageUrl
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Cập nhật sản phẩm thành công!"
            );

            return "redirect:/admin/product/page";

        } catch (RuntimeException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            model.addAttribute(
                    "product",
                    productService.getProductById(productId)
            );

            loadFormData(model);

            return "admin/product/edit";
        }
    }


    // ================================
    // DELETE PRODUCT
    // ================================

    @PostMapping("/delete/{productId}")
    public String deleteProduct(
            @PathVariable Integer productId,
            RedirectAttributes redirectAttributes) {

        try {

            productService.deleteProduct(productId);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Xóa sản phẩm thành công!"
            );

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/product/page";
    }


    // ================================
    // LOAD FORM DATA
    // ================================

    private void loadFormData(Model model) {

        model.addAttribute(
                "categoryList",
                categoryService.getAllCategories()
        );

        model.addAttribute(
                "sizeList",
                sizeService.getAllSizes()
        );

        model.addAttribute(
                "statusList",
                ProductStatus.values()
        );
    }
}