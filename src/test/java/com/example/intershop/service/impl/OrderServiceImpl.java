package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.model.Order;
import com.example.intershop.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderServiceImpl orderService;

    private Item item;

    @BeforeEach
    void setUp() {
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
        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.placeOrder(cart);

        Mockito.verify(orderRepository).save(captor.capture());
        Order captured = captor.getValue();

        assertEquals(1, captured.getItems().size());
        assertEquals(42L, result.getId());
    }

    @Test
    void getAllOrders_shouldReturnListOfOrders() {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getOrderById_shouldReturnCorrectOrder() {
        Order order = new Order();
        order.setId(99L);
        Mockito.when(orderRepository.findById(99L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderById(99L);

        assertTrue(result.isPresent());
        assertEquals(99L, result.get().getId());
    }

    @Test
    void getOrderById_shouldReturnEmptyIfNotFound() {
        Mockito.when(orderRepository.findById(123L)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrderById(123L);

        assertTrue(result.isEmpty());
    }
}
