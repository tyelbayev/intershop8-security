package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderItem;
import com.example.intershop.repository.OrderRepository;
import com.example.intershop.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Mono<Order> placeOrder(Map<Item, Integer> items) {
        Order order = new Order();
        order.setItems(new ArrayList<>());

        items.forEach((item, quantity) -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(quantity);
            order.getItems().add(orderItem);
        });

        return orderRepository.save(order);
    }

    @Override
    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
}
