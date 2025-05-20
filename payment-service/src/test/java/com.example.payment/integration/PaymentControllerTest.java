package com.example.payment.integration;

import com.example.payment.model.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnBalanceForUser() {
        webTestClient.get()
                .uri("/balance/user1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Double.class)
                .value(balance -> assertThat(balance).isGreaterThan(0.0));
    }

    @Test
    void shouldPaySuccessfully_whenBalanceSufficient() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user1");
        request.setAmount(100.0);

        webTestClient.post()
                .uri("/pay")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true);
    }

    @Test
    void shouldFailPayment_whenBalanceLow() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user2");
        request.setAmount(1000.0);

        webTestClient.post()
                .uri("/pay")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }
}
