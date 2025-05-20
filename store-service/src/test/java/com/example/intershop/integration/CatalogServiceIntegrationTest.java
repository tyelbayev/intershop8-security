package com.example.intershop.integration;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import com.example.intershop.service.CatalogService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CatalogServiceIntegrationTest {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private ItemRepository itemRepository;

    @SpyBean
    private ItemRepository spyItemRepository;

    @Autowired
    private ReactiveRedisTemplate<String, Item> redisTemplate;

    private Item item;

    @BeforeEach
    void setup() {
        item = new Item();
        item.setTitle("Cached Item");
        item.setPrice(BigDecimal.valueOf(99));
        item = itemRepository.save(item).block();
    }

    @AfterEach
    void clearRedis() {
        redisTemplate.delete("item:" + item.getId()).block();
    }

    @Test
    void getItemById_shouldCacheInRedis() {
        Long id = item.getId();

        StepVerifier.create(catalogService.getItemById(id))
                .expectNextMatches(i -> i.getTitle().equals("Cached Item"))
                .verifyComplete();

        StepVerifier.create(catalogService.getItemById(id))
                .expectNextMatches(i -> i.getTitle().equals("Cached Item"))
                .verifyComplete();

        Mockito.verify(spyItemRepository, Mockito.times(1)).findById(id);
    }
}

