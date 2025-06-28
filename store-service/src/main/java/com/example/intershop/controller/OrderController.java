package com.example.intershop.controller;

import com.example.intershop.service.CartService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.CartService.ItemWithQuantity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;

    public OrderController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @PostMapping("/buy")
    public Mono<String> placeOrder() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(username ->
                        cartService.getItems(username)
                                .collectMap(ItemWithQuantity::item, ItemWithQuantity::quantity)
                                .flatMap(cart -> orderService.placeOrder(username, cart))
                                .flatMap(order ->
                                        cartService.clear(username)
                                                .thenReturn("redirect:/orders/" + order.getId() + "?newOrder=true")
                                )
                )
                .onErrorResume(ex -> {
                    ex.printStackTrace();
                    return Mono.just("redirect:/main/items?error=true");
                });
    }


    @GetMapping("/orders")
    public Mono<Rendering> getOrders() {
        return orderService.getAllOrders()
                .collectList()
                .map(orders -> Rendering.view("orders")
                        .modelAttribute("orders", orders)
                        .build());
    }

    @GetMapping("/orders/{id}")
    public Mono<Rendering> getOrder(@PathVariable Long id,
                                    @RequestParam(defaultValue = "false") boolean newOrder) {
        return orderService.getOrderById(id)
                .map(order -> Rendering.view("order")
                        .modelAttribute("order", order)
                        .modelAttribute("newOrder", newOrder)
                        .build());
    }
}
