package com.example.intershop.service;

import com.example.intershop.model.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CatalogService {
    Flux<Item> getItems(String search, String sort, int pageNumber, int pageSize);
    Mono<Item> getItemById(Long id);
}
