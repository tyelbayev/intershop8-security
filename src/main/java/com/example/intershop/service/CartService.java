package com.example.intershop.service;

import com.example.intershop.model.Item;

import java.math.BigDecimal;
import java.util.Map;

public interface CartService {
    void addItem(Long itemId);
    void removeItem(Long itemId);
    void deleteItem(Long itemId);
    Map<Item, Integer> getItems();
    BigDecimal getTotal();
    boolean isEmpty();
    void clear();
}

