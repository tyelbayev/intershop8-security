package com.example.intershop.service.impl;

import com.example.intershop.client.PaymentClient;
import com.example.intershop.model.Item;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderItem;
import com.example.intershop.repository.OrderItemRepository;
import com.example.intershop.repository.OrderRepository;
import com.example.intershop.service.OrderService;
import com.example.intershop.util.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
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
    public Mono<Order> placeOrder(String username, Map<Item,Integer> items) {
        BigDecimal total = calculateTotal(items);

        return paymentClient.getBalance(username)
                .doOnNext(balance -> log.info("Balance for {}: {}", username, balance))
                .flatMap(balance -> {
                    if (BigDecimal.valueOf(balance).compareTo(total) < 0) {
                        return Mono.error(new RuntimeException("Недостаточно средств"));
                    }

                    return createAndSaveOrder(items, total, username)
                            .doOnNext(order -> log.info("Order created: {}", order.getId()))
                            .flatMap(savedOrder ->
                                    paymentClient.pay(username, total)
                                            .doOnNext(success -> log.info("Payment result: {}", success))
                                            .defaultIfEmpty(false)
                                            .flatMap(success -> {
                                                if (Boolean.TRUE.equals(success)) {
                                                    return Mono.just(savedOrder);
                                                }
                                                return Mono.error(new RuntimeException("Ошибка при списании средств"));
                                            })
                            );
                })
                .switchIfEmpty(Mono.error(new IllegalStateException("placeOrder produced empty")));
    }

    private BigDecimal calculateTotal(Map<Item, Integer> items) {
        return items.entrySet().stream()
                .map(e -> e.getKey().getPrice().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Mono<Order> createAndSaveOrder(Map<Item,Integer> items,
                                           BigDecimal total,
                                           String username) {

        if (items.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Cart is empty"));
        }

        Order order = new Order();
        order.setTotalSum(total);
        order.setUsername(username);

        return orderRepository.save(order)
                .flatMap(savedOrder -> {
                    List<OrderItem> toPersist = items.entrySet().stream()
                            .map(e -> new OrderItem(savedOrder, e.getKey(), e.getValue()))
                            .toList();

                    return orderItemRepository
                            .saveAll(toPersist)
                            .collectList()
                            .map(list -> {
                                savedOrder.setItems(list);
                                return savedOrder;
                            });
                });
    }

    @Override
    public Flux<Order> getAllOrders() {
        return CurrentUser.getPreferredUsername()
                .flatMapMany(orderRepository::findAllByUsername);
    }

    @Override
    public Mono<Order> getOrderById(Long id) {
        return CurrentUser.getPreferredUsername()
                .flatMap(username ->
                        orderRepository.findById(id)
                                .flatMap(order -> {
                                    if (!order.getUsername().equals(username)) {
                                        return Mono.error(new AccessDeniedException("Доступ запрещён"));
                                    }
                                    return Mono.just(order);
                                }));
    }
}
