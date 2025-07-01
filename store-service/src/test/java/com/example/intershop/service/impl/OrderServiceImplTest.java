package com.example.intershop.service.impl;

import com.example.intershop.client.PaymentClient;
import com.example.intershop.model.Item;
import com.example.intershop.model.Order;
import com.example.intershop.model.OrderItem;
import com.example.intershop.repository.OrderItemRepository;
import com.example.intershop.repository.OrderRepository;
import com.example.intershop.service.OrderService;
import com.example.intershop.util.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
//
//    private OrderRepository orderRepository;
//    private OrderItemRepository orderItemRepository;
//    private PaymentClient paymentClient;
//    private OrderService orderService;
//
//    private Item item;
//
//    @BeforeEach
//    void setUp() {
//        orderRepository = mock(OrderRepository.class);
//        orderItemRepository = mock(OrderItemRepository.class);
//        paymentClient = mock(PaymentClient.class);
//        orderService = new OrderServiceImpl(orderRepository, orderItemRepository, paymentClient);
//
//        item = new Item();
//        item.setId(1L);
//        item.setTitle("Test Item");
//        item.setPrice(BigDecimal.valueOf(100));
//    }
//
//    @Test
//    void placeOrder_shouldSaveOrderWithItems() {
//        Map<Item, Integer> cart = Map.of(item, 2);
//        Order savedOrder = new Order();
//        savedOrder.setId(42L);
//
//        when(paymentClient.getBalance("user1")).thenReturn(Mono.just(1000.0));
//        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(savedOrder));
//        when(orderItemRepository.saveAll(anyList())).thenAnswer(invocation ->
//                Flux.fromIterable((List<OrderItem>) invocation.getArgument(0))
//        );
//        when(paymentClient.pay(eq("user1"), eq(BigDecimal.valueOf(200)))).thenReturn(Mono.just(true));
//
//        StepVerifier.create(orderService.placeOrder("user1", cart))
//                .expectNextMatches(order -> order.getId().equals(42L))
//                .verifyComplete();
//
//        verify(orderRepository).save(any());
//        verify(orderItemRepository).saveAll(anyList());
//        verify(paymentClient).pay("user1", BigDecimal.valueOf(200));
//    }
//
//    @Test
//    void placeOrder_shouldFail_whenBalanceTooLow() {
//        Map<Item, Integer> cart = Map.of(item, 2);
//        when(paymentClient.getBalance("user1")).thenReturn(Mono.just(100.0)); // нужно 200
//
//        StepVerifier.create(orderService.placeOrder("user1", cart))
//                .expectErrorMatches(e -> e instanceof RuntimeException &&
//                        e.getMessage().contains("Недостаточно средств"))
//                .verify();
//
//        verify(orderRepository, never()).save(any());
//        verify(orderItemRepository, never()).saveAll((Iterable<OrderItem>) any());
//        verify(paymentClient, never()).pay(any(), any());
//    }
//
//    @Test
//    void getAllOrders_shouldReturnOrders() {
//        String username = "user1";
//        Order order = new Order();
//        order.setId(1L);
//        order.setUsername(username);
//
//        when(orderRepository.findAllByUsername(username)).thenReturn(Flux.just(order));
//
//        // создаём реального OidcUser
//        Map<String, Object> claims = Map.of("preferred_username", username);
//        OidcIdToken idToken = new OidcIdToken("token", Instant.now(), Instant.now().plusSeconds(60), claims);
//        OidcUserInfo userInfo = new OidcUserInfo(claims);
//        DefaultOidcUser oidcUser = new DefaultOidcUser(Collections.emptyList(), idToken, userInfo, "preferred_username");
//
//        Authentication auth = new UsernamePasswordAuthenticationToken(oidcUser, "password", oidcUser.getAuthorities());
//
//        StepVerifier.create(
//                        orderService.getAllOrders()
//                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
//                )
//                .expectNextMatches(o -> o.getId().equals(1L))
//                .verifyComplete();
//    }
//
//
//
//
//
//    @Test
//    void getOrderById_shouldReturnOrder() {
//        try (MockedStatic<CurrentUser> mockedUser = mockStatic(CurrentUser.class)) {
//            Order order = new Order();
//            order.setId(99L);
//            order.setUsername("testuser");
//
//            mockedUser.when(CurrentUser::getPreferredUsername).thenReturn(Mono.just("testuser"));
//            when(orderRepository.findById(99L)).thenReturn(Mono.just(order));
//
//            StepVerifier.create(orderService.getOrderById(99L))
//                    .expectNextMatches(o -> o.getId().equals(99L))
//                    .verifyComplete();
//        }
//    }
//
//
//    @Test
//    void getOrderById_shouldReturnEmpty() {
//        try (MockedStatic<CurrentUser> mockedUser = mockStatic(CurrentUser.class)) {
//            mockedUser.when(CurrentUser::getPreferredUsername).thenReturn(Mono.just("testuser"));
//
//            when(orderRepository.findById(123L)).thenReturn(Mono.empty());
//
//            StepVerifier.create(orderService.getOrderById(123L))
//                    .verifyComplete();
//        }
//    }

}

