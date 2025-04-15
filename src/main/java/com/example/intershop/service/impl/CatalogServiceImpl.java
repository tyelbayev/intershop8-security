package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import com.example.intershop.service.CatalogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final ItemRepository itemRepository;

    public CatalogServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
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
        return itemRepository.findById(id);
    }

    private Comparator<Item> getComparator(String sort) {
        return switch (sort) {
            case "ALPHA" -> Comparator.comparing(Item::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "PRICE" -> Comparator.comparing(Item::getPrice);
            default -> Comparator.comparing(Item::getId); // fallback
        };
    }
}
