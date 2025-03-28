package com.example.intershop.service;

import com.example.intershop.model.Item;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CatalogService {
    Page<Item> getItems(String search, String sort, int pageNumber, int pageSize);
    Optional<Item> getItemById(Long id);
}