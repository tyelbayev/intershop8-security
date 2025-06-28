package com.example.intershop.service;

import com.example.intershop.model.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CartService {
    Mono<Void> addItem(String username, Long itemId);
    Mono<Void> removeItem(String username, Long itemId);
    Mono<Void> deleteItem(String username, Long itemId);

    Flux<ItemWithQuantity> getItems(String username);
    Mono<BigDecimal> getTotal(String username);
    Mono<Boolean> isEmpty(String username);
    Mono<Void> clear(String username);

    record ItemWithQuantity(Item item, int quantity) {}
}

