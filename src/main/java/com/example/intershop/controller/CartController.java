package com.example.intershop.controller;

import com.example.intershop.model.Item;
import com.example.intershop.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/cart/items")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String getCart(Model model) {
        Map<Item, Integer> items = cartService.getItems();
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.getTotal());
        model.addAttribute("empty", cartService.isEmpty());
        return "cart";
    }

    @PostMapping("/{id}")
    public String updateCart(@PathVariable Long id, @RequestParam String action) {
        switch (action) {
            case "PLUS" -> cartService.addItem(id);
            case "MINUS" -> cartService.removeItem(id);
            case "DELETE" -> cartService.deleteItem(id);
        }
        return "redirect:/cart/items";
    }
}

