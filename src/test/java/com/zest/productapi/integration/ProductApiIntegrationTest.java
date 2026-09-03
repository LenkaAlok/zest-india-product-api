package com.zest.productapi.integration;
import com.zest.productapi.entity.Item;
import com.zest.productapi.entity.Product;
import com.zest.productapi.repository.ItemRepository;
import com.zest.productapi.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
class ProductApiIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ItemRepository itemRepository;


    @BeforeEach
    void setUp() {

        itemRepository.deleteAll();
        productRepository.deleteAll();
    }


    @Test
    void testProductAndItemIntegration() {

        // Create Product
        Product product = new Product();

        product.setProductName("Integration Test Product");
        product.setCreatedBy("Admin_123");
        product.setCreatedOn(LocalDateTime.now());
        Product savedProduct =
                productRepository.save(product);


        // Create Item
        Item item = new Item();

        item.setProduct(savedProduct);
        item.setQuantity(50);

        Item savedItem =
                itemRepository.save(item);


        // Fetch product from database
        Product foundProduct =
                productRepository.findById(savedProduct.getId())
                        .orElse(null);


        assertNotNull(foundProduct);

        assertEquals(
                "Integration Test Product",
                foundProduct.getProductName()
        );


        // Fetch items using product ID
        List<Item> items =
                itemRepository.findByProductId(
                        savedProduct.getId()
                );


        assertNotNull(items);

        assertEquals(1, items.size());

        assertEquals(
                savedItem.getId(),
                items.get(0).getId()
        );

        assertEquals(
                50,
                items.get(0).getQuantity()
        );

        assertEquals(
                savedProduct.getId(),
                items.get(0).getProduct().getId()
        );
    }
}
