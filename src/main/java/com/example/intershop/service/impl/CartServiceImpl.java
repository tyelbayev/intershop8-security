package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import com.example.intershop.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope
public class CartServiceImpl implements CartService {

    private final ItemRepository itemRepository;
    private final Map<Long, Integer> cart = new HashMap<>();

    public CartServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Mono<Void> addItem(Long itemId) {
        cart.merge(itemId, 1, Integer::sum);
        return Mono.empty();
    }

    @Override
    public Mono<Void> removeItem(Long itemId) {
        cart.computeIfPresent(itemId, (id, count) -> (count > 1) ? count - 1 : null);
        return Mono.empty();
    }

    @Override
    public Mono<Void> deleteItem(Long itemId) {
        cart.remove(itemId);
        return Mono.empty();
    }

    @Override
    public Flux<ItemWithQuantity> getItems() {
        return Flux.fromIterable(cart.entrySet())
                .flatMap(entry ->
                        itemRepository.findById(entry.getKey())
                                .map(item -> new ItemWithQuantity(item, entry.getValue()))
                );
    }

    @Override
    public Mono<BigDecimal> getTotal() {
        return getItems()
                .map(iq -> iq.item().getPrice().multiply(BigDecimal.valueOf(iq.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Mono<Boolean> isEmpty() {
        return Mono.just(cart.isEmpty());
    }

    @Override
    public Mono<Void> clear() {
        cart.clear();
        return Mono.empty();
    }
}
