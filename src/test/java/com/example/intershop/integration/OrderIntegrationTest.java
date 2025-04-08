package com.example.intershop.integration;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    private Long itemId;

    @BeforeEach
    void setup() throws Exception {
        Item item = new Item();
        item.setTitle("Laptop");
        item.setDescription("Gaming laptop");
        item.setPrice(BigDecimal.valueOf(1500));
        item.setCount(3);
        item.setImgPath("/img/laptop.jpg");
        itemId = Objects.requireNonNull(itemRepository.save(item).block()).getId();

        mockMvc.perform(post("/cart/items/" + itemId).param("action", "PLUS"));
    }

    @Test
    void placeOrder_shouldRedirectToOrderPage() throws Exception {
        MvcResult result = mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getRedirectedUrl();
        assertTrue(redirectedUrl.contains("/orders/"));
    }

    @Test
    void viewOrders_shouldShowOrderList() throws Exception {
        mockMvc.perform(post("/buy"));
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }
}
