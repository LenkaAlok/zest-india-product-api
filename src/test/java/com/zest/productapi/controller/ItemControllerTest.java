package com.zest.productapi.controller;
import com.zest.productapi.dto.ItemResponseDto;
import com.zest.productapi.service.ItemService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        ItemController itemController =
                new ItemController(itemService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
    }


    @Test
    void testGetItemsByProductId() throws Exception {

        ItemResponseDto item1 = new ItemResponseDto();
        item1.setId(101);
        item1.setProductId(1);
        item1.setQuantity(10);

        ItemResponseDto item2 = new ItemResponseDto();
        item2.setId(102);
        item2.setProductId(1);
        item2.setQuantity(20);


        when(itemService.getItemsByProductId(1))
                .thenReturn(List.of(item1, item2));


        mockMvc.perform(
                        get("/api/v1/products/1/items")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].quantity").value(10))

                .andExpect(jsonPath("$[1].id").value(102))
                .andExpect(jsonPath("$[1].productId").value(1))
                .andExpect(jsonPath("$[1].quantity").value(20));


        verify(itemService, times(1))
                .getItemsByProductId(1);
    }


    @Test
    void testGetItemsByProductIdWhenNoItemsFound()
            throws Exception {

        when(itemService.getItemsByProductId(1))
                .thenReturn(List.of());


        mockMvc.perform(
                        get("/api/v1/products/1/items")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));


        verify(itemService, times(1))
                .getItemsByProductId(1);
    }
}