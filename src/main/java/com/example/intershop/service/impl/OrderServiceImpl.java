package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderItem;
import com.example.intershop.repository.OrderItemRepository;
import com.example.intershop.repository.OrderRepository;
import com.example.intershop.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional
    public Mono<Order> placeOrder(Map<Item, Integer> items) {
        Order order = new Order();

        List<OrderItem> orderItems = items.entrySet().stream()
                .map(entry -> {
                    OrderItem oi = new OrderItem();
                    oi.setOrder(order);
                    oi.setItem(entry.getKey());
                    oi.setQuantity(entry.getValue());
                    return oi;
                })
                .toList();

        order.setItems(orderItems);

        BigDecimal total = orderItems.stream()
                .map(oi -> oi.getItem().getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalSum(total);
        System.out.println("##order:" + order.getTotalSum());
        return orderRepository.save(order)
                .flatMap(savedOrder -> {
                    orderItems.forEach(oi -> oi.setOrder(savedOrder));
                    return orderItemRepository.saveAll(orderItems)
                            .collectList()
                            .map(savedItems -> {
                                savedOrder.setItems(savedItems);
                                return savedOrder;
                            });
                });
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
