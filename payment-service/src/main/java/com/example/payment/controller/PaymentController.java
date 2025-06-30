package com.example.payment.controller;

import com.example.payment.api.BalanceApi;
import com.example.payment.api.PayApi;
import com.example.payment.model.PayPost200Response;
import com.example.payment.model.PaymentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class PaymentController implements BalanceApi, PayApi {

    private final Map<String, BigDecimal> balances = new ConcurrentHashMap<>();

    public PaymentController() {
        balances.put("user1", BigDecimal.valueOf(100000));
        balances.put("user2", BigDecimal.valueOf(500));
    }

    @Override
    public Mono<ResponseEntity<Double>> balanceUserIdGet(String userId, ServerWebExchange exchange) {
        if (!balances.containsKey(userId)) {
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        }
        return Mono.just(ResponseEntity.ok(balances.get(userId).doubleValue()));

    }

    @Override
    public Mono<ResponseEntity<PayPost200Response>> payPost(Mono<PaymentRequest> paymentRequestMono, ServerWebExchange exchange) {
        return paymentRequestMono.flatMap(request -> {
            BigDecimal current = balances.getOrDefault(request.getUserId(), BigDecimal.ZERO);
            BigDecimal amount = BigDecimal.valueOf(request.getAmount());

            PayPost200Response response = new PayPost200Response();

            if (current.compareTo(amount) >= 0) {
                balances.put(request.getUserId(), current.subtract(amount));
                response.setSuccess(true);
                return Mono.just(ResponseEntity.ok(response));
            } else {
                response.setSuccess(false);
                return Mono.just(ResponseEntity.badRequest().body(response));
            }
        });
    }



}
