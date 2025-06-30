package com.example.intershop.controller;
import com.example.intershop.service.CartService;
import com.example.intershop.service.OrderService;
import com.example.intershop.service.CartService.ItemWithQuantity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
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
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .cast(DefaultOidcUser.class)
                .mapNotNull(oidcUser -> {
                    String username = (String) oidcUser.getAttribute("preferred_username");
                    log.info("BUY requested by username = {}", username);
                    return username;
                })
                .switchIfEmpty(Mono.just("redirect:/login"))
                .flatMap(username -> {
                    if (username.startsWith("redirect:")) {
                        return Mono.just(username);
                    }
                    return cartService.getItems(username)
                            .collectMap(ItemWithQuantity::item, ItemWithQuantity::quantity)
                            .flatMap(cart -> {
                                if (cart.isEmpty()) {
                                    return Mono.just("redirect:/main/items?emptyCart=true");
                                }
                                return orderService.placeOrder(username, cart)
                                        .flatMap(order -> {
                                            if (order == null) {
                                                return Mono.error(new IllegalStateException("Order is null from service"));
                                            }
                                            return cartService.clear(username)
                                                    .thenReturn("redirect:/orders/" + order.getId() + "?newOrder=true");
                                        });
                            });
                })
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
