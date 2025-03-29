package com.example.intershop.integration;

import com.example.intershop.model.Item;
import com.example.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CatalogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();

        Item item = new Item();
        item.setTitle("title");
        item.setDescription("title desc");
        item.setPrice(BigDecimal.valueOf(999));
        item.setImgPath("/img/title.jpg");
        item.setCount(10);
        itemRepository.save(item);
    }

    @Test
    void getItems_shouldReturnMainTemplate() throws Exception {
        mockMvc.perform(get("/main/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("pageNumber"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("hasNext"))
                .andExpect(model().attributeExists("hasPrevious"));
    }
}
