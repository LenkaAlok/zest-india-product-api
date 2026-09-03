package com.zest.productapi.service;
import com.zest.productapi.entity.Item;
import com.zest.productapi.entity.Product;
import com.zest.productapi.repository.ItemRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;


    @Test
    void testGetItemsByProductId() {

        Product product = new Product();
        product.setId(1);

        Item item1 = new Item();
        item1.setId(101);
        item1.setProduct(product);
        item1.setQuantity(10);

        Item item2 = new Item();
        item2.setId(102);
        item2.setProduct(product);
        item2.setQuantity(20);

        when(itemRepository.findByProductId(1))
                .thenReturn(List.of(item1, item2));


        List<com.zest.productapi.dto.ItemResponseDto> result =
                itemService.getItemsByProductId(1);


        assertNotNull(result);

        assertEquals(2, result.size());

        assertEquals(101, result.get(0).getId());
        assertEquals(1, result.get(0).getProductId());
        assertEquals(10, result.get(0).getQuantity());

        assertEquals(102, result.get(1).getId());
        assertEquals(1, result.get(1).getProductId());
        assertEquals(20, result.get(1).getQuantity());


        verify(itemRepository, times(1))
                .findByProductId(1);
    }


    @Test
    void testGetItemsByProductIdWhenNoItemsFound() {

        when(itemRepository.findByProductId(1))
                .thenReturn(List.of());


        List<com.zest.productapi.dto.ItemResponseDto> result =
                itemService.getItemsByProductId(1);


        assertNotNull(result);

        assertEquals(0, result.size());


        verify(itemRepository, times(1))
                .findByProductId(1);
    }
}