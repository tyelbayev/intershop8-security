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

    private ItemRepository itemRepository;
    private CartService cartService;
    private Item testItem;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        cartService = new CartServiceImpl(itemRepository);

        testItem = new Item();
        testItem.setId(1L);
        testItem.setTitle("item title");
        testItem.setPrice(BigDecimal.valueOf(100));
    }

    @Test
    void addItem_shouldIncreaseQuantity() {
        when(itemRepository.findById(1L)).thenReturn(Mono.just(testItem));

        cartService.addItem(1L);
        cartService.addItem(1L);

        StepVerifier.create(cartService.getItems())
                .expectNextMatches(iq ->
                        iq.item().equals(testItem) && iq.quantity() == 2
                )
                .verifyComplete();
    }

    @Test
    void removeItem_shouldDecreaseQuantityOrRemove() {
        when(itemRepository.findById(1L)).thenReturn(Mono.just(testItem));

        cartService.addItem(1L);
        cartService.addItem(1L);
        cartService.removeItem(1L);

        StepVerifier.create(cartService.getItems())
                .expectNextMatches(iq ->
                        iq.item().equals(testItem) && iq.quantity() == 1
                )
                .verifyComplete();

        cartService.removeItem(1L);

        StepVerifier.create(cartService.getItems())
                .expectComplete()
                .verify();
    }

    @Test
    void deleteItem_shouldCompletelyRemoveFromCart() {
        cartService.addItem(1L);
        cartService.deleteItem(1L);

        StepVerifier.create(cartService.getItems())
                .expectComplete()
                .verify();
    }

    @Test
    void getTotal_shouldReturnCorrectSum() {
        when(itemRepository.findById(1L)).thenReturn(Mono.just(testItem));

        cartService.addItem(1L);
        cartService.addItem(1L);

        StepVerifier.create(cartService.getTotal())
                .expectNext(BigDecimal.valueOf(200))
                .verifyComplete();
    }

    @Test
    void clear_shouldEmptyCart() {
        cartService.addItem(1L);
        cartService.clear();

        StepVerifier.create(cartService.getItems())
                .expectComplete()
                .verify();
    }

    @Test
    void isEmpty_shouldReflectCartState() {
        assertTrue(cartService.isEmpty().block());
        cartService.addItem(1L);
        assertFalse(cartService.isEmpty().block());
    }
}
