package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import com.example.intershop.service.CatalogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class CatalogServiceImpl implements CatalogService {

    private final ItemRepository itemRepository;

    private final ReactiveRedisTemplate<String, Item> redisTemplate;

    public CatalogServiceImpl(ItemRepository itemRepository,
                              ReactiveRedisTemplate<String, Item> redisTemplate) {
        this.itemRepository = itemRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Flux<Item> getItems(String search, String sort, int pageNumber, int pageSize) {
        Sort sorting = getSort(sort);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sorting);

        return (search == null || search.isBlank())
                ? itemRepository.findAll(sorting).skip((long) (pageNumber - 1) * pageSize).take(pageSize)
                : itemRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, sorting)
                .skip((long) (pageNumber - 1) * pageSize)
                .take(pageSize);
    }

    private Sort getSort(String sort) {
        return switch (sort) {
            case "ALPHA" -> Sort.by("title").ascending();
            case "PRICE" -> Sort.by("price").ascending();
            default -> Sort.unsorted();
        };
    }


    @Override
    public Mono<Item> getItemById(Long id) {
        String key = "item:" + id;

        return redisTemplate.opsForValue()
                .get(key)
                .switchIfEmpty(
                        itemRepository.findById(id)
                                .flatMap(item -> redisTemplate.opsForValue()
                                        .set(key, item)
                                        .thenReturn(item))
                );
    }
}
