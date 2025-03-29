package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import com.example.intershop.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final ItemRepository itemRepository;
    private final Map<Long, Integer> cart = new HashMap<>();

    public CartServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void addItem(Long itemId) {
        cart.merge(itemId, 1, Integer::sum);
    }

    @Override
    public void removeItem(Long itemId) {
        cart.computeIfPresent(itemId, (id, count) -> (count > 1) ? count - 1 : null);
    }

    @Override
    public void deleteItem(Long itemId) {
        cart.remove(itemId);
    }

    @Override
    public Map<Item, Integer> getItems() {
        return cart.entrySet().stream()
                .map(entry -> Map.entry(itemRepository.findById(entry.getKey()).orElseThrow(), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public BigDecimal getTotal() {
        return getItems().entrySet().stream()
                .map(e -> e.getKey().getPrice().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean isEmpty() {
        return cart.isEmpty();
    }

    @Override
    public void clear() {
        cart.clear();
    }
}
