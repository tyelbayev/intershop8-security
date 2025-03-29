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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    private Long itemId;

    @BeforeEach
    void setup() {
        Item item = new Item();
        item.setTitle("book");
        item.setDescription("book1");
        item.setPrice(BigDecimal.valueOf(20));
        item.setCount(5);
        item.setImgPath("/img/book.jpg");
        itemId = itemRepository.save(item).getId();
    }

    @Test
    void addItemToCart_thenGetCart() throws Exception {
        mockMvc.perform(post("/cart/items/" + itemId)
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("empty", false));
    }
}

