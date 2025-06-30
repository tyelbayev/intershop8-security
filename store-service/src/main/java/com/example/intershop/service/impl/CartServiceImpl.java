package com.example.intershop.service.impl;

import com.example.intershop.repository.ItemRepository;
import com.example.intershop.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
//@SessionScope
public class CartServiceImpl implements CartService {

    private final ItemRepository itemRepository;
    private final Map<String, Map<Long, Integer>> userCarts = new ConcurrentHashMap<>();

    public CartServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    private Map<Long, Integer> getUserCart(String username) {
        Map<Long, Integer> cart = userCarts.computeIfAbsent(username, u -> new ConcurrentHashMap<>());
        log.info("Cart for {} → {}", username, cart);
        return cart;
    }


    @Override
    public Mono<Void> addItem(String username, Long itemId) {
        getUserCart(username).merge(itemId, 1, Integer::sum);
        return Mono.empty();
    }

    @Override
    public Mono<Void> removeItem(String username, Long itemId) {
        getUserCart(username).computeIfPresent(itemId, (id, count) -> (count > 1) ? count - 1 : null);
        return Mono.empty();
    }

    @Override
    public Mono<Void> deleteItem(String username, Long itemId) {
        getUserCart(username).remove(itemId);
        return Mono.empty();
    }

    @Override
    public Flux<ItemWithQuantity> getItems(String username) {
        log.info("getItems() for {} → {} items", username, getUserCart(username).size());
        return Flux.fromIterable(getUserCart(username).entrySet())
                .flatMap(entry ->
                        itemRepository.findById(entry.getKey())
                                .map(item -> new ItemWithQuantity(item, entry.getValue()))
                );
    }

    @Override
    public Mono<BigDecimal> getTotal(String username) {
        return getItems(username)
                .map(iq -> iq.item().getPrice().multiply(BigDecimal.valueOf(iq.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Mono<Boolean> isEmpty(String username) {
        return Mono.just(getUserCart(username).isEmpty());
    }

    @Override
    public Mono<Void> clear(String username) {
        getUserCart(username).clear();
        return Mono.empty();
    }
}
