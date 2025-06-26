package com.example.intershop.integration;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CartIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemRepository itemRepository;

    private Long itemId;

    @BeforeEach
    void setup() {
        Item item = new Item();
        item.setTitle("book");
        item.setDescription("book1");
        item.setPrice(BigDecimal.valueOf(20));
        item.setCount(5);
        item.setImgPath("/img/book.jpg");

        itemId = itemRepository.save(item).block().getId(); // обязательно .block() для сохранения перед тестом
    }

//    @Test
//    void addItemToCart_thenGetCart() {
//        webTestClient.post()
//                .uri("/cart/items/{id}?action=PLUS", itemId)
//                .exchange()
//                .expectStatus().is3xxRedirection();
//
//        webTestClient.get()
//                .uri("/cart/items")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(String.class)
//                .consumeWith(response -> {
//                    String html = response.getResponseBody();
//                    assert html != null;
//                    assert html.contains("book");
//                    assert html.contains("Итого");
//                });
//    }
}
