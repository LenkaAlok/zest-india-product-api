package com.zest.productapi.controller;
import com.zest.productapi.dto.ProductRequestDto;
import com.zest.productapi.dto.ProductResponseDto;
import com.zest.productapi.exception.GlobalExceptionHandler;
import com.zest.productapi.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @Mock
    private ProductService productService;
    private MockMvc mockMvc;
    private ProductController productController;

    @BeforeEach
    void setUp() {
        productController = new ProductController(productService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }


    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @Test
    void testGetAllProducts() throws Exception {

        ProductResponseDto product1 = new ProductResponseDto();
        product1.setId(1);
        product1.setProductName("Laptop");

        ProductResponseDto product2 = new ProductResponseDto();
        product2.setId(2);
        product2.setProductName("Keyboard");

        Page<ProductResponseDto> productPage =
                new PageImpl<>(
                        List.of(product1, product2),
                        PageRequest.of(0, 10),
                        2
                );

        when(productService.getAllProducts(any()))
                .thenReturn(productPage);

        mockMvc.perform(
                        get("/api/v1/products")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].productName").value("Laptop"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].productName").value("Keyboard"));

        verify(productService, times(1))
                .getAllProducts(any(Pageable.class));
    }
    // GET PRODUCT BY ID
    @Test
    void testGetProductById() throws Exception {

        ProductResponseDto response = new ProductResponseDto();

        response.setId(1);
        response.setProductName("Laptop");
        response.setCreatedBy("Admin_123");
        response.setCreatedOn(LocalDateTime.now());

        when(productService.getProductById(1))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/products/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.createdBy").value("Admin_123"));

        verify(productService, times(1))
                .getProductById(1);
    }
    // CREATE PRODUCT
    @Test
    void testCreateProduct() throws Exception {

        ProductResponseDto response = new ProductResponseDto();

        response.setId(1);
        response.setProductName("Laptop");
        response.setCreatedBy("Admin_123");

        when(productService.createProduct(any(ProductRequestDto.class)))
                .thenReturn(response);

        String requestJson = """
                {
                    "productName": "Laptop"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.createdBy").value("Admin_123"));

        verify(productService, times(1))
                .createProduct(any(ProductRequestDto.class));
    }

    // UPDATE PRODUCT

    @Test
    void testUpdateProduct() throws Exception {

        ProductResponseDto response = new ProductResponseDto();

        response.setId(1);
        response.setProductName("Keyboard");
        response.setCreatedBy("Admin_123");
        response.setModifiedBy("Admin_123");
        response.setModifiedOn(LocalDateTime.now());

        when(productService.updateProduct(
                eq(1),
                any(ProductRequestDto.class)
        )).thenReturn(response);

        String requestJson = """
                {
                    "productName": "Keyboard"
                }
                """;

        mockMvc.perform(
                        put("/api/v1/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Keyboard"))
                .andExpect(jsonPath("$.modifiedBy").value("Admin_123"));

        verify(productService, times(1))
                .updateProduct(
                        eq(1),
                        any(ProductRequestDto.class)
                );
    }

    // DELETE PRODUCT

    @Test
    void testDeleteProduct() throws Exception {

        doNothing()
                .when(productService)
                .deleteProduct(1);

        mockMvc.perform(
                        delete("/api/v1/products/1")
                )
                .andExpect(status().isNoContent());

        verify(productService, times(1))
                .deleteProduct(1);
    }
}