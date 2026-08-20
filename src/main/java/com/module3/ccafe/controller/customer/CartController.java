package com.module3.ccafe.controller.customer;

import com.module3.ccafe.dto.Cart;
import com.module3.ccafe.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    public String addItem(@RequestParam Integer productId, HttpSession session) {
        cartService.addItem(productId, session);
        return "redirect:/menu";
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Cart cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        return "customer/menu";
    }

    @PostMapping("/items/{productId}/increase")
    public String increase(@PathVariable Integer productId, HttpSession session) {
        cartService.increase(productId, session);
        return "redirect:/menu";
    }

    @PostMapping("/items/{productId}/decrease")
    public String decrease(@PathVariable Integer productId, HttpSession session) {
        cartService.decrease(productId, session);
        return "redirect:/menu";
    }

    @PostMapping("/items/{productId}/remove")
    public String remove(@PathVariable Integer productId, HttpSession session) {
        cartService.removeItem(productId, session);
        return "redirect:/menu";
    }

    @PostMapping("/place-order")
    public String placeOrder(@RequestParam Map<String, String> params,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Map<Integer, String> notes = new HashMap<>();
        params.forEach((key, value) -> {
            if (key.startsWith("notes[")) {
                String productIdString = key.substring(6, key.length() - 1);
                Integer productId = Integer.valueOf(productIdString);
                notes.put(productId, value);
            }
        });
        try {
            cartService.placeOrder(session, notes);
            redirectAttributes.addFlashAttribute("success", "Gọi món thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/menu";
    }
}
