package com.module3.ccafe.controller.customer;

import com.module3.ccafe.dto.Cart;
import com.module3.ccafe.entity.CafeTable;
import com.module3.ccafe.entity.Category;
import com.module3.ccafe.entity.Product;
import com.module3.ccafe.entity.enums.TableStatus;
import com.module3.ccafe.repository.CafeTableRepository;
import com.module3.ccafe.service.CartService;
import com.module3.ccafe.service.ICategoryService;
import com.module3.ccafe.service.IProductService;
import com.module3.ccafe.service.OrderService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MenuController {
    private final ICategoryService categoryService;
    private final IProductService productService;
    private final CartService cartService;
    private final CafeTableRepository cafeTableRepository;

    @GetMapping("/menu")
    public String showMenu(@RequestParam(required = false) Integer categoryId,
                           @RequestParam(required = false) String keyword,
                           HttpSession session,
                           Model model) {
        Integer tableId = (Integer) session.getAttribute("TABLE_ID");
        if (tableId != null) {
            CafeTable table = cafeTableRepository.findById(tableId).orElse(null);
            model.addAttribute("currentTable", table);
        }
        List<Category> categories = categoryService.findAll();
        List<Product> products = productService.findAvailableProducts();
        List<Map<String, Object>> categoryList = categories.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("categoryId", c.getCategoryId());
            map.put("name", c.getName());
            return map;
        }).collect(Collectors.toList());

        List<Map<String, Object>> productList = products.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", p.getProductId());
            map.put("name", p.getName());
            map.put("price", p.getPrice());
            map.put("image", p.getImage());
            map.put("category", p.getCategory() != null ? p.getCategory().getCategoryId() : null);
            map.put("status",p.getStatus());
            return map;
        }).collect(Collectors.toList());
        
        ObjectMapper mapper = new ObjectMapper();
        Cart cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        model.addAttribute("categoriesJson", mapper.writeValueAsString(categoryList));
        model.addAttribute("productsJson", mapper.writeValueAsString(productList));
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("keyword", keyword);
        return "customer/menu";
    }
}
