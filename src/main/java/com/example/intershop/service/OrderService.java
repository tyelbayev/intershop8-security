package com.example.intershop.service;

import com.example.intershop.model.Item;
import com.example.intershop.model.Order;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
    Order placeOrder(Map<Item, Integer> items);
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long id);
}

