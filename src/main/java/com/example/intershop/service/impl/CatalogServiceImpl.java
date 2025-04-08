package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import com.example.intershop.service.CatalogService;
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
        Flux<Item> source = (search == null || search.isBlank())
                ? itemRepository.findAll()
                : itemRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);

        // сортировка и пагинация — вручную (в памяти)
        return source
                .sort(getComparator(sort))
                .skip((long) (pageNumber - 1) * pageSize)
                .take(pageSize);
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
