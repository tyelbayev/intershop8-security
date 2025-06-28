package com.example.intershop.service.impl;

import com.example.intershop.client.PaymentClient;
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
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentClient paymentClient;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentClient = paymentClient;
    }

    @Override
    @Transactional
    public Mono<Order> placeOrder(Map<Item, Integer> items) {
        String userId = "user1"; 

        BigDecimal total = calculateTotal(items);

        return paymentClient.getBalance(userId)
                .flatMap(balance -> {
                    if (BigDecimal.valueOf(balance).compareTo(total) < 0) {
                        return Mono.error(new RuntimeException("Недостаточно средств на балансе"));
                    }

                    return createAndSaveOrder(items, total)
                            .flatMap(savedOrder ->
                                    paymentClient.pay(userId, total)
                                            .flatMap(success -> {
                                                if (success) {
                                                    return Mono.just(savedOrder);
                                                } else {
                                                    return Mono.error(new RuntimeException("Ошибка при списании средств"));
                                                }
                                            })
                            );
                });
    }

    private BigDecimal calculateTotal(Map<Item, Integer> items) {
        return items.entrySet().stream()
                .map(e -> e.getKey().getPrice().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Mono<Order> createAndSaveOrder(Map<Item, Integer> items, BigDecimal total) {
        Order order = new Order();
        order.setTotalSum(total);

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
    public Flux<Order> getAllOrders(String username) {
        return orderRepository.findAllByUsername(username);
    }

    @Override
    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
}
