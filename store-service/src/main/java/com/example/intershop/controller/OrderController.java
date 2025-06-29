package com.example.intershop.controller;
import com.example.intershop.service.CartService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.CartService.ItemWithQuantity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.reactive.result.view.View;
import reactor.core.publisher.Mono;
@Slf4j

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

        return ReactiveSecurityContextHolder.getContext()               // Mono<SecurityContext>
                .map(ctx -> ctx.getAuthentication().getName())          // Mono<String> (username)
                .switchIfEmpty(Mono.just("redirect:/login"))            // неавторизован – на логин
                .flatMap(username -> {

                    // если уже redirect (строка начинается с "redirect:")
                    if (username.startsWith("redirect:")) {
                        return Mono.just(username);
                    }

                    /* 1. Получаем корзину */
                    return cartService.getItems(username)               // Flux<ItemWithQuantity>
                            .collectMap(ItemWithQuantity::item,
                                    ItemWithQuantity::quantity)     // Mono<Map<Item,Integer>>
                            .flatMap(cart -> {

                                /* 2. Пустая корзина – на список товаров */
                                if (cart.isEmpty()) {
                                    return Mono.just("redirect:/main/items?emptyCart=true");
                                }

                                /* 3. Делаем заказ */
                                return orderService.placeOrder(username, cart)   // Mono<Order>
                                        .flatMap(order -> {
                                            /* 3a. Страховка от null (не должно быть) */
                                            if (order == null) {
                                                return Mono.error(new IllegalStateException(
                                                        "Order is null from service"));
                                            }
                                            /* 3b. Очищаем корзину и редиректим */
                                            return cartService.clear(username)   // Mono<Void>
                                                    .thenReturn("redirect:/orders/"
                                                            + order.getId()
                                                            + "?newOrder=true");
                                        });
                            });
                })
                /* 4. Любая бизнес/системная ошибка → /main/items?error=true */
                .onErrorResume(ex -> {
                    log.error("Place order failed", ex);
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
