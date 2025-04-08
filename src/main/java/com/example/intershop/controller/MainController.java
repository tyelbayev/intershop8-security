package com.example.intershop.controller;

import com.example.intershop.model.Item;
import com.example.intershop.service.CartService;
import com.example.intershop.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/main/items")
public class MainController {

    private final CatalogService catalogService;
    private final CartService cartService;

    public MainController(CatalogService catalogService, CartService cartService) {
        this.catalogService = catalogService;
        this.cartService = cartService;
    }

    @GetMapping
    public Mono<Rendering> getItems(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "NO") String sort,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber
    ) {
        return catalogService.getItems(search, sort, pageNumber, pageSize)
                .collectList()
                .map(items -> {
                    List<List<Item>> itemsGrid = splitToGrid(items, 3);
                    return Rendering.view("main")
                            .modelAttribute("items", itemsGrid)
                            .modelAttribute("search", search)
                            .modelAttribute("sort", sort)
                            .modelAttribute("pageSize", pageSize)
                            .modelAttribute("pageNumber", pageNumber)
                            .modelAttribute("hasPrevious", pageNumber > 1)
                            .modelAttribute("hasNext", items.size() == pageSize)
                            .build();
                });
    }

    @PostMapping("/{id}")
    public Mono<String> updateCart(@PathVariable Long id, @RequestParam String action) {
        return switch (action) {
            case "PLUS" -> cartService.addItem(id).thenReturn("redirect:/main/items");
            case "MINUS" -> cartService.removeItem(id).thenReturn("redirect:/main/items");
            case "DELETE" -> cartService.deleteItem(id).thenReturn("redirect:/main/items");
            default -> Mono.just("redirect:/main/items");
        };
    }

    private List<List<Item>> splitToGrid(List<Item> items, int perRow) {
        List<List<Item>> grid = new ArrayList<>();
        for (int i = 0; i < items.size(); i += perRow) {
            grid.add(items.subList(i, Math.min(i + perRow, items.size())));
        }
        return grid;
    }
}
