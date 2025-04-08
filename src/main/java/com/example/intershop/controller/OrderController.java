package com.example.intershop.controller;

import com.example.intershop.model.Order;
import com.example.intershop.service.CartService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.CartService.ItemWithQuantity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.util.List;

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
        return cartService.getItems()
                .collectMap(ItemWithQuantity::item, ItemWithQuantity::quantity)
                .flatMap(orderService::placeOrder)
                .doOnSuccess(order -> cartService.clear().subscribe()) // очищаем корзину
                .map(order -> "redirect:/orders/" + order.getId() + "?newOrder=true");
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
