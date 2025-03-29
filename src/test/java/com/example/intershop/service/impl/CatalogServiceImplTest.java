package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CatalogServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    private CatalogServiceImpl catalogService;

    private List<Item> allItems;

    @BeforeEach
    void setUp() {
        catalogService = new CatalogServiceImpl(itemRepository);
        Item a = new Item(); a.setId(1L); a.setTitle("title1"); a.setDescription("title desc1"); a.setPrice(BigDecimal.valueOf(50));
        Item b = new Item(); b.setId(2L); b.setTitle("title2"); b.setDescription("title desc2"); b.setPrice(BigDecimal.valueOf(30));
        allItems = List.of(a, b);
    }

    @Test
    void getItems_noSearch_noSort_returnsPagedList() {
        Mockito.when(itemRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(allItems.subList(0, 2)));
        Page<Item> result = catalogService.getItems("", "NO", 1, 2);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void getItems_searchFiltersResults() {
        Mockito.when(itemRepository.search("title2"))
                .thenReturn(List.of(allItems.get(1)));
        Page<Item> result = catalogService.getItems("title2", "NO", 1, 10);
        assertEquals(1, result.getContent().size());
        assertEquals("title2", result.getContent().get(0).getTitle());
    }

    @Test
    void getItems_sortAlpha_shouldSortByTitle() {
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by("title"));
        Mockito.when(itemRepository.findAll(expectedPageable))
                .thenReturn(new PageImpl<>(allItems));
        Page<Item> result = catalogService.getItems("", "ALPHA", 1, 10);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void getItemById_existingItem_shouldReturnItem() {
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(allItems.get(0)));
        Optional<Item> result = catalogService.getItemById(1L);
        assertTrue(result.isPresent());
        assertEquals("title1", result.get().getTitle());
    }

    @Test
    void getItemById_notFound_shouldReturnEmpty() {
        Mockito.when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Item> result = catalogService.getItemById(99L);
        assertTrue(result.isEmpty());
    }
}
