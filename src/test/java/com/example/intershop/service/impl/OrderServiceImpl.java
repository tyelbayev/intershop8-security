package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.model.Order;
import com.example.intershop.repository.OrderRepository;
import com.example.intershop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private OrderRepository orderRepository;
    private OrderService orderService;

    private Item item;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderService = new OrderServiceImpl(orderRepository);

        item = new Item();
        item.setId(1L);
        item.setTitle("Test Item");
        item.setPrice(BigDecimal.valueOf(100));
    }

    @Test
    void placeOrder_shouldSaveOrderWithItems() {
        Map<Item, Integer> cart = Map.of(item, 2);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        Order savedOrder = new Order();
        savedOrder.setId(42L);
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(savedOrder));

        StepVerifier.create(orderService.placeOrder(cart))
                .expectNextMatches(order -> order.getId().equals(42L))
                .verifyComplete();

        verify(orderRepository).save(captor.capture());
        Order captured = captor.getValue();
        assertEquals(1, captured.getItems().size());
    }

    @Test
    void getAllOrders_shouldReturnOrders() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findAll()).thenReturn(Flux.just(order));

        StepVerifier.create(orderService.getAllOrders())
                .expectNextMatches(o -> o.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void getOrderById_shouldReturnOrder() {
        Order order = new Order();
        order.setId(99L);
        when(orderRepository.findById(99L)).thenReturn(Mono.just(order));

        StepVerifier.create(orderService.getOrderById(99L))
                .expectNextMatches(o -> o.getId().equals(99L))
                .verifyComplete();
    }

    @Test
    void getOrderById_shouldReturnEmpty() {
        when(orderRepository.findById(123L)).thenReturn(Mono.empty());

        StepVerifier.create(orderService.getOrderById(123L))
                .verifyComplete();
    }
}
