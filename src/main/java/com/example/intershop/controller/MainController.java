package com.example.intershop.controller;

import com.example.intershop.model.Item;
import com.example.intershop.service.CartService;
import com.example.intershop.service.CatalogService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public String getItems(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "NO") String sort,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model
    ) {
        Page<Item> page = catalogService.getItems(search, sort, pageNumber, pageSize);
        List<List<Item>> itemsGrid = splitToGrid(page.getContent(), 3);

        model.addAttribute("items", itemsGrid);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", Map.of(
                "pageNumber", pageNumber,
                "pageSize", pageSize,
                "hasNext", page.hasNext(),
                "hasPrevious", page.hasPrevious()
        ));

        return "main";
    }

    @PostMapping("/{id}")
    public String updateCart(@PathVariable Long id, @RequestParam String action) {
        handleCartAction(id, action);
        return "redirect:/main/items";
    }

    private void handleCartAction(Long id, String action) {
        switch (action) {
            case "PLUS" -> cartService.addItem(id);
            case "MINUS" -> cartService.removeItem(id);
            case "DELETE" -> cartService.deleteItem(id);
        }
    }

    private List<List<Item>> splitToGrid(List<Item> items, int perRow) {
        List<List<Item>> grid = new ArrayList<>();
        for (int i = 0; i < items.size(); i += perRow) {
            grid.add(items.subList(i, Math.min(i + perRow, items.size())));
        }
        return grid;
    }
}
