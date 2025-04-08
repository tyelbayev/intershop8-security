package com.example.intershop.service;

import com.example.intershop.model.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CartService {

    Mono<Void> addItem(Long itemId);
    Mono<Void> removeItem(Long itemId);
    Mono<Void> deleteItem(Long itemId);

    Flux<ItemWithQuantity> getItems(); // реактивный список товаров с количеством
    Mono<BigDecimal> getTotal();
    Mono<Boolean> isEmpty();
    Mono<Void> clear();

    record ItemWithQuantity(Item item, int quantity) {}
}
