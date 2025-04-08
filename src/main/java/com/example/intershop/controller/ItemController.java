package com.example.intershop.controller;

import com.example.intershop.service.CartService;
import com.example.intershop.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

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
    public Mono<Rendering> getItem(@PathVariable Long id) {
        return catalogService.getItemById(id)
                .map(item -> Rendering.view("item")
                        .modelAttribute("item", item)
                        .build()
                );
    }

    @PostMapping("/{id}")
    public Mono<String> updateCart(@PathVariable Long id, @RequestParam String action) {
        return switch (action) {
            case "PLUS" -> cartService.addItem(id).thenReturn("redirect:/items/" + id);
            case "MINUS" -> cartService.removeItem(id).thenReturn("redirect:/items/" + id);
            case "DELETE" -> cartService.deleteItem(id).thenReturn("redirect:/items/" + id);
            default -> Mono.just("redirect:/items/" + id);
        };
    }
}
