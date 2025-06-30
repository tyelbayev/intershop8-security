package com.example.intershop.controller;

import com.example.intershop.model.Item;
import com.example.intershop.service.CartService;
import com.example.intershop.service.CatalogService;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
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
                .doOnNext(item -> System.out.println("###Item: " + item))
                .collectList()
                .doOnNext(list -> System.out.println("Items count: " + list.size()))
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

    @GetMapping("/{id}")
    public Mono<String> updateCart(@PathVariable Long id, @RequestParam String action) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser)
                        ctx.getAuthentication().getPrincipal())
                .map(user -> user.getAttribute("preferred_username"))
                .flatMap(username -> {
                    Mono<Void> result = switch (action.toUpperCase()) {
                        case "PLUS", "ADD" -> cartService.addItem(username.toString(), id);
                        case "MINUS" -> cartService.removeItem(username.toString(), id);
                        case "DELETE" -> cartService.deleteItem(username.toString(), id);
                        default -> Mono.empty();
                    };
                    return result.thenReturn("redirect:/main/items");
                });
    }


    private List<List<Item>> splitToGrid(List<Item> items, int perRow) {
        List<List<Item>> grid = new ArrayList<>();
        for (int i = 0; i < items.size(); i += perRow) {
            grid.add(items.subList(i, Math.min(i + perRow, items.size())));
        }
        return grid;
    }
}
