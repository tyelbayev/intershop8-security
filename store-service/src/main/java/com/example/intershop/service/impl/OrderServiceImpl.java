package com.example.intershop.service.impl;

import com.example.intershop.client.PaymentClient;
import com.example.intershop.model.Item;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderItem;
import com.example.intershop.repository.OrderItemRepository;
import com.example.intershop.repository.OrderRepository;
import com.example.intershop.service.OrderService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
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
    public Mono<Order> placeOrder(String username, Map<Item,Integer> items) {

        BigDecimal total = calculateTotal(items);

        return paymentClient.getBalance(username)               // Mono<BigDecimal>
                .flatMap(balance -> {
                    if (BigDecimal.valueOf(balance).compareTo(total) < 0) {
                        return Mono.error(new RuntimeException("Недостаточно средств"));
                    }

                    return createAndSaveOrder(items, total, username)
                            .flatMap(savedOrder ->
                                    paymentClient.pay(username, total)  // Mono<Boolean>
                                            .defaultIfEmpty(false)          // ← на случай empty
                                            .flatMap(success -> {
                                                if (Boolean.TRUE.equals(success)) {
                                                    return Mono.just(savedOrder);
                                                }
                                                return Mono.error(
                                                        new RuntimeException("Ошибка при списании средств"));
                                            })
                            );
                })
                .switchIfEmpty(Mono.error(
                        new IllegalStateException("placeOrder produced empty")));
    }




    private BigDecimal calculateTotal(Map<Item, Integer> items) {
        return items.entrySet().stream()
                .map(e -> e.getKey().getPrice().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Mono<Order> createAndSaveOrder(Map<Item,Integer> items,
                                           BigDecimal total,
                                           String username) {

        if (items.isEmpty()) {                         // пустая корзина – бизнес-ошибка
            return Mono.error(new IllegalArgumentException("Cart is empty"));
        }

        Order order = new Order();
        order.setTotalSum(total);
        order.setUsername(username);

        return orderRepository.save(order)             // Mono<Order>
                .flatMap(savedOrder -> {

                    List<OrderItem> toPersist = items.entrySet().stream()
                            .map(e -> new OrderItem(savedOrder, e.getKey(), e.getValue()))
                            .toList();

                    return orderItemRepository
                            .saveAll(toPersist)            // Flux<OrderItem>
                            .collectList()                 // Mono<List<OrderItem>>
                            .map(list -> {
                                savedOrder.setItems(list);
                                return savedOrder;         // ← никогда null
                            });
                });
    }


    @Override
    public Flux<Order> getAllOrders() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .flatMapMany(orderRepository::findAllByUsername);
    }

    @Override
    public Mono<Order> getOrderById(Long id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    String username = auth.getName();
                    return orderRepository.findById(id)
                            .flatMap(order -> {
                                if (!order.getUsername().equals(username)) {
                                    return Mono.error(new AccessDeniedException("Доступ запрещён"));
                                }
                                return Mono.just(order);
                            });
                });
    }

}
