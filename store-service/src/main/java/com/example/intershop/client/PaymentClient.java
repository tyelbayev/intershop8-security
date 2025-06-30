package com.example.intershop.client;

import com.example.payment.client.model.PayPost200Response;
import com.example.payment.client.model.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentClient {

    private final WebClient paymentWebClient;

    public Mono<Double> getBalance(String username) {
        return paymentWebClient.get()
                .uri("/balance/{username}", username)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    return Mono.error(new RuntimeException("User not found in balance service"));
                })

                .bodyToMono(Double.class);
    }




    public Mono<Boolean> pay(String userId, BigDecimal amount) {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setAmount(amount.doubleValue());

        return paymentWebClient.post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                        Mono.error(new RuntimeException("Payment service error: " + response.statusCode()))
                )
                .bodyToMono(PayPost200Response.class)
                .map(PayPost200Response::getSuccess);
    }

}
