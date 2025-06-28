package com.example.intershop.controller;

import com.example.intershop.service.CartService;
import com.example.intershop.service.CartService.ItemWithQuantity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
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
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(username -> {
                    Mono<List<ItemWithQuantity>> itemsMono = cartService.getItems(username).collectList();
                    Mono<BigDecimal> totalMono = cartService.getTotal(username);
                    Mono<Boolean> emptyMono = cartService.isEmpty(username);

                    return Mono.zip(itemsMono, totalMono, emptyMono)
                            .map(tuple -> Rendering.view("cart")
                                    .modelAttribute("items", tuple.getT1())
                                    .modelAttribute("total", tuple.getT2())
                                    .modelAttribute("empty", tuple.getT3())
                                    .build());
                });
    }

    @PostMapping("/{id}")
    public Mono<String> updateCart(@PathVariable Long id, @RequestParam String action) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(username -> switch (action) {
                    case "PLUS" -> cartService.addItem(username, id).thenReturn("redirect:/cart/items");
                    case "MINUS" -> cartService.removeItem(username, id).thenReturn("redirect:/cart/items");
                    case "DELETE" -> cartService.deleteItem(username, id).thenReturn("redirect:/cart/items");
                    default -> Mono.just("redirect:/cart/items");
                });
    }
}

