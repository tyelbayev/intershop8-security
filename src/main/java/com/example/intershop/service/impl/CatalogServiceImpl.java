package com.example.intershop.service.impl;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import com.example.intershop.service.CatalogService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final ItemRepository itemRepository;

    public CatalogServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Page<Item> getItems(String search, String sort, int pageNumber, int pageSize) {
        Pageable pageable;
        switch (sort) {
            case "ALPHA" -> pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("title"));
            case "PRICE" -> pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("price"));
            default -> pageable = PageRequest.of(pageNumber - 1, pageSize);
        }

        if (search == null || search.isBlank()) {
            return itemRepository.findAll(pageable);
        } else {
            List<Item> filtered = itemRepository.search(search);
            int start = Math.min((pageNumber - 1) * pageSize, filtered.size());
            int end = Math.min(start + pageSize, filtered.size());
            return new PageImpl<>(filtered.subList(start, end), pageable, filtered.size());
        }
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }
}

