package com.example.intershop.integration;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import com.example.intershop.service.OrderService;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemRepository itemRepository;

    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8080))
            .build();

    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setTitle("Test Item");
        testItem.setPrice(BigDecimal.valueOf(100));
        itemRepository.save(testItem).block();
    }

    @Test
    void placeOrder_shouldSucceed_whenBalanceIsSufficient() {
        wiremock.stubFor(get(urlPathMatching("/balance/user1"))
                .willReturn(okJson("1000")));

        wiremock.stubFor(post(urlPathEqualTo("/pay"))
                .willReturn(okJson("{ \"success\": true }")));

        Map<Item, Integer> cart = Map.of(testItem, 2);

        StepVerifier.create(orderService.placeOrder(cart))
                .expectNextMatches(order -> order.getTotalSum().compareTo(BigDecimal.valueOf(200)) == 0)
                .verifyComplete();
    }

    @Test
    void placeOrder_shouldFail_whenBalanceIsLow() {
        wiremock.stubFor(get(urlPathMatching("/balance/user1"))
                .willReturn(okJson("50")));

        Map<Item, Integer> cart = Map.of(testItem, 2);

        StepVerifier.create(orderService.placeOrder(cart))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("Недостаточно средств"))
                .verify();
    }
}
