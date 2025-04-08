package com.example.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class RedirectController {

    @GetMapping("/")
    public Mono<String> redirectToMain() {
        return Mono.just("redirect:/main/items");
    }
}
