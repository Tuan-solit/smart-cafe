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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryViewController {

    private final CategoryService categoryService;

    @GetMapping("/page")
    public String categoryPage(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        if (page < 0) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("categoryId").ascending()
        );

        Page<CategoryResponse> categoryPage =
                categoryService.searchCategories(
                        keyword.trim(),
                        pageable
                );

        model.addAttribute(
                "categoryList",
                categoryPage.getContent()
        );

        model.addAttribute(
                "categoryPage",
                categoryPage
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        model.addAttribute(
                "pageSize",
                size
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
                    "Ngừng hoạt động danh mục thành công!"
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