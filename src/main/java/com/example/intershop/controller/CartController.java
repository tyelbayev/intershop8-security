package com.example.intershop.controller;

import com.example.intershop.service.CartService;
import com.example.intershop.service.CartService.ItemWithQuantity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart/items")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Mono<Rendering> getCart() {
        Mono<List<ItemWithQuantity>> itemsMono = cartService.getItems().collectList();
        Mono<BigDecimal> totalMono = cartService.getTotal();
        Mono<Boolean> emptyMono = cartService.isEmpty();

        return Mono.zip(itemsMono, totalMono, emptyMono)
                .map(tuple -> Rendering.view("cart")
                        .modelAttribute("items", tuple.getT1())
                        .modelAttribute("total", tuple.getT2())
                        .modelAttribute("empty", tuple.getT3())
                        .build());
    }

    @PostMapping("/{id}")
    public Mono<String> updateCart(@PathVariable Long id, @RequestParam String action) {
        return switch (action) {
            case "PLUS" -> cartService.addItem(id).thenReturn("redirect:/cart/items");
            case "MINUS" -> cartService.removeItem(id).thenReturn("redirect:/cart/items");
            case "DELETE" -> cartService.deleteItem(id).thenReturn("redirect:/cart/items");
            default -> Mono.just("redirect:/cart/items");
        };
    }
}
