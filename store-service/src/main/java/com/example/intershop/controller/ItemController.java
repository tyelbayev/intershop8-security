package com.example.intershop.controller;

import com.example.intershop.service.CartService;
import com.example.intershop.service.CatalogService;
import com.example.intershop.util.CurrentUser;
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
        return CurrentUser.getPreferredUsername()
                .flatMap(username -> {
                    Mono<Void> result = switch (action) {
                        case "PLUS" -> cartService.addItem(username, id);
                        case "MINUS" -> cartService.removeItem(username, id);
                        case "DELETE" -> cartService.deleteItem(username, id);
                        default -> Mono.empty();
                    };
                    return result.thenReturn("redirect:/items/" + id);
                });
    }
}
