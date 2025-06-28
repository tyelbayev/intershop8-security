package com.example.intershop.service;

import com.example.intershop.model.Item;
import com.example.intershop.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface OrderService {
    Mono<Order> placeOrder(String username, Map<Item, Integer> items);
    Flux<Order> getAllOrders();
    Mono<Order> getOrderById(Long id);
}
