package com.example.intershop.controller;

import com.example.intershop.model.Item;
import com.example.intershop.service.CartService;
import com.example.intershop.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final CatalogService catalogService;
    private final CartService cartService;

    public ItemController(CatalogService catalogService, CartService cartService) {
        this.catalogService = catalogService;
        this.cartService = cartService;
    }

    @GetMapping("/{id}")
    public String getItem(@PathVariable Long id, Model model) {
        Item item = catalogService.getItemById(id).orElseThrow();
        model.addAttribute("item", item);
        return "item";
    }

    @PostMapping("/{id}")
    public String updateCart(@PathVariable Long id, @RequestParam String action) {
        switch (action) {
            case "PLUS" -> cartService.addItem(id);
            case "MINUS" -> cartService.removeItem(id);
            case "DELETE" -> cartService.deleteItem(id);
        }
        return "redirect:/items/" + id;
    }
}
