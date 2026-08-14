package com.module3.ccafe.controller.admin;

import com.module3.ccafe.dto.CategoryRequest;
import com.module3.ccafe.dto.CategoryResponse;
import com.module3.ccafe.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryViewController {

    private final CategoryService categoryService;

    @GetMapping("/page")
    public String categoryPage(Model model) {

        model.addAttribute(
                "categoryList",
                categoryService.getAllCategories()
        );

        return "admin/category/list";
    }

    @GetMapping("/create")
    public String createPage(Model model) {

        model.addAttribute(
                "categoryRequest",
                new CategoryRequest()
        );

        return "admin/category/create";
    }

    @PostMapping("/create")
    public String createCategory(
            @ModelAttribute("categoryRequest") CategoryRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/category/create";
        }

        try {

            categoryService.createCategory(request);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Thêm danh mục thành công!"
            );

            return "redirect:/admin/category/page";

        } catch (RuntimeException e) {

            String message = e.getMessage();

            if ("Tên danh mục đã tồn tại".equals(message)) {

                bindingResult.rejectValue(
                        "name",
                        "duplicate.name",
                        "Tên danh mục này đã tồn tại"
                );

            } else {

                model.addAttribute("error", message);
            }

            return "admin/category/create";
        }
    }

    @GetMapping("/edit/{categoryId}")
    public String editPage(
            @PathVariable Integer categoryId,
            Model model) {

        CategoryResponse category =
                categoryService.getCategoryById(categoryId);

        CategoryRequest request = CategoryRequest.builder()
                .name(category.getName())
                .build();

        model.addAttribute("categoryRequest", request);
        model.addAttribute("category", category);

        return "admin/category/edit";
    }

    @PostMapping("/edit/{categoryId}")
    public String updateCategory(
            @PathVariable Integer categoryId,
            @ModelAttribute("categoryRequest") CategoryRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "category",
                    categoryService.getCategoryById(categoryId)
            );

            return "admin/category/edit";
        }

        try {

            categoryService.updateCategory(
                    categoryId,
                    request
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Cập nhật danh mục thành công!"
            );

            return "redirect:/admin/category/page";

        } catch (RuntimeException e) {

            String message = e.getMessage();

            if ("Tên danh mục đã tồn tại".equals(message)) {

                bindingResult.rejectValue(
                        "name",
                        "duplicate.name",
                        "Tên danh mục này đã tồn tại"
                );

            } else {

                model.addAttribute("error", message);
            }

            model.addAttribute(
                    "category",
                    categoryService.getCategoryById(categoryId)
            );

            return "admin/category/edit";
        }
    }

    @PostMapping("/delete/{categoryId}")
    public String deleteCategory(
            @PathVariable Integer categoryId,
            RedirectAttributes redirectAttributes) {

        try {

            categoryService.deleteCategory(categoryId);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Xóa danh mục thành công!"
            );

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/category/page";
    }
}