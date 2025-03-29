package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    private CartServiceImpl cartService;

    private Item testItem;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(itemRepository);

        testItem = new Item();
        testItem.setId(1L);
        testItem.setPrice(BigDecimal.valueOf(100));
        testItem.setTitle("item title");
    }

    @Test
    void addItem_shouldIncreaseQuantity() {
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        cartService.addItem(1L);
        cartService.addItem(1L);

        Map<Item, Integer> items = cartService.getItems();

        assertEquals(1, items.size());
        assertEquals(2, items.get(testItem));
    }

    @Test
    void removeItem_shouldDecreaseQuantityOrRemove() {
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        cartService.addItem(1L);
        cartService.addItem(1L);
        cartService.removeItem(1L);

        Map<Item, Integer> items = cartService.getItems();
        assertEquals(1, items.get(testItem));

        cartService.removeItem(1L);
        assertTrue(cartService.getItems().isEmpty());
    }

    @Test
    void deleteItem_shouldCompletelyRemoveFromCart() {
        cartService.addItem(1L);
        cartService.deleteItem(1L);
        assertTrue(cartService.getItems().isEmpty());
    }

    @Test
    void getTotal_shouldReturnCorrectSum() {
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        cartService.addItem(1L);
        cartService.addItem(1L);

        BigDecimal total = cartService.getTotal();
        assertEquals(BigDecimal.valueOf(200), total);
    }

    @Test
    void clear_shouldEmptyCart() {
        cartService.addItem(1L);
        cartService.clear();
        assertTrue(cartService.getItems().isEmpty());
    }

    @Test
    void isEmpty_shouldReflectCartState() {
        assertTrue(cartService.isEmpty());
        cartService.addItem(1L);
        assertFalse(cartService.isEmpty());
    }
}

