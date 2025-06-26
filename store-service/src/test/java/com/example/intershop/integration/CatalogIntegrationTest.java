package com.example.intershop.integration;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CatalogIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setup() {
        itemRepository.deleteAll().block();

        Item item = new Item();
        item.setTitle("title");
        item.setDescription("title desc");
        item.setPrice(BigDecimal.valueOf(999));
        item.setImgPath("/img/title.jpg");
        item.setCount(10);
        itemRepository.save(item).block();
    }

//    @Test
//    void getItems_shouldReturnMainTemplate() {
//        webTestClient.get()
//                .uri("/main/items")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(String.class)
//                .value(body -> {
//                    assert body.contains("main");
//                    assert body.contains("title");
//                });
//    }
}
