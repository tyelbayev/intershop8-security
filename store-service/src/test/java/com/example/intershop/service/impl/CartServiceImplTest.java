package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import com.example.intershop.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
//
//    private ItemRepository itemRepository;
//    private CartService cartService;
//    private Item testItem;
//    private final String user = "testuser";
//
//    @BeforeEach
//    void setUp() {
//        itemRepository = mock(ItemRepository.class);
//        cartService = new CartServiceImpl(itemRepository);
//
//        testItem = new Item();
//        testItem.setId(1L);
//        testItem.setTitle("item title");
//        testItem.setPrice(BigDecimal.valueOf(100));
//    }
//
//    @Test
//    void addItem_shouldIncreaseQuantity() {
//        when(itemRepository.findById(1L)).thenReturn(Mono.just(testItem));
//
//        cartService.addItem(user, 1L).block();
//        cartService.addItem(user, 1L).block();
//
//        StepVerifier.create(cartService.getItems(user))
//                .expectNextMatches(iq ->
//                        iq.item().equals(testItem) && iq.quantity() == 2
//                )
//                .verifyComplete();
//    }
//
//    @Test
//    void removeItem_shouldDecreaseQuantityOrRemove() {
//        when(itemRepository.findById(1L)).thenReturn(Mono.just(testItem));
//
//        cartService.addItem(user, 1L).block();
//        cartService.addItem(user, 1L).block();
//        cartService.removeItem(user, 1L).block();
//
//        StepVerifier.create(cartService.getItems(user))
//                .expectNextMatches(iq ->
//                        iq.item().equals(testItem) && iq.quantity() == 1
//                )
//                .verifyComplete();
//
//        cartService.removeItem(user, 1L).block();
//
//        StepVerifier.create(cartService.getItems(user))
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    void deleteItem_shouldCompletelyRemoveFromCart() {
//        cartService.addItem(user, 1L).block();
//        cartService.deleteItem(user, 1L).block();
//
//        StepVerifier.create(cartService.getItems(user))
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    void getTotal_shouldReturnCorrectSum() {
//        when(itemRepository.findById(1L)).thenReturn(Mono.just(testItem));
//
//        cartService.addItem(user, 1L).block();
//        cartService.addItem(user, 1L).block();
//
//        StepVerifier.create(cartService.getTotal(user))
//                .expectNext(BigDecimal.valueOf(200))
//                .verifyComplete();
//    }
//
//    @Test
//    void clear_shouldEmptyCart() {
//        cartService.addItem(user, 1L).block();
//        cartService.clear(user).block();
//
//        StepVerifier.create(cartService.getItems(user))
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    void isEmpty_shouldReflectCartState() {
//        assertTrue(cartService.isEmpty(user).block());
//        cartService.addItem(user, 1L).block();
//        assertFalse(cartService.isEmpty(user).block());
//    }
}
