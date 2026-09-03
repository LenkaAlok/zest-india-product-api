package com.zest.productapi.service;
import com.zest.productapi.dto.ProductRequestDto;
import com.zest.productapi.dto.ProductResponseDto;
import com.zest.productapi.entity.Product;
import com.zest.productapi.exception.ProductNotFoundException;
import com.zest.productapi.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;


    // ---------------------------------------------------------
    // CREATE PRODUCT
    // ---------------------------------------------------------

    @Test
    void testCreateProduct() {

        // Arrange
        ProductRequestDto request = new ProductRequestDto();
        request.setProductName("Laptop");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("Admin_123");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        Product savedProduct = new Product();
        savedProduct.setId(1);
        savedProduct.setProductName("Laptop");
        savedProduct.setCreatedBy("Admin_123");
        savedProduct.setCreatedOn(LocalDateTime.now());

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        // Act
        ProductResponseDto response =
                productService.createProduct(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Laptop", response.getProductName());
        assertEquals("Admin_123", response.getCreatedBy());

        verify(productRepository, times(1))
                .save(any(Product.class));
    }


    // ---------------------------------------------------------
    // GET ALL PRODUCTS
    // ---------------------------------------------------------

    @Test
    void testGetAllProducts() {

        // Arrange
        Product product1 = new Product();
        product1.setId(1);
        product1.setProductName("Laptop");
        product1.setCreatedBy("Admin_123");

        Product product2 = new Product();
        product2.setId(2);
        product2.setProductName("Keyboard");
        product2.setCreatedBy("Admin_123");

        List<Product> products = List.of(product1, product2);

        Page<Product> productPage =
                new PageImpl<>(products);

        Pageable pageable =
                PageRequest.of(0, 10);

        when(productRepository.findAll(pageable))
                .thenReturn(productPage);

        // Act
        Page<ProductResponseDto> response =
                productService.getAllProducts(pageable);

        // Assert
        assertNotNull(response);

        assertEquals(2, response.getContent().size());

        assertEquals(
                "Laptop",
                response.getContent().get(0).getProductName()
        );

        assertEquals(
                "Keyboard",
                response.getContent().get(1).getProductName()
        );

        verify(productRepository, times(1))
                .findAll(pageable);
    }


    // ---------------------------------------------------------
    // GET PRODUCT BY ID - SUCCESS
    // ---------------------------------------------------------

    @Test
    void testGetProductById_WhenProductExists() {

        // Arrange
        Product product = new Product();
        product.setId(1);
        product.setProductName("Laptop");
        product.setCreatedBy("Admin_123");

        when(productRepository.findById(1))
                .thenReturn(Optional.of(product));

        // Act
        ProductResponseDto response =
                productService.getProductById(1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Laptop", response.getProductName());
        assertEquals("Admin_123", response.getCreatedBy());

        verify(productRepository, times(1))
                .findById(1);
    }


    // ---------------------------------------------------------
    // GET PRODUCT BY ID - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void testGetProductById_WhenProductDoesNotExist() {

        // Arrange
        when(productRepository.findById(999))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(999)
        );

        verify(productRepository, times(1))
                .findById(999);
    }


    // ---------------------------------------------------------
    // UPDATE PRODUCT - SUCCESS
    // ---------------------------------------------------------

    @Test
    void testUpdateProduct_WhenProductExists() {

        // Arrange
        ProductRequestDto request = new ProductRequestDto();
        request.setProductName("Keyboard");

        Product existingProduct = new Product();
        existingProduct.setId(1);
        existingProduct.setProductName("Laptop");
        existingProduct.setCreatedBy("Admin_123");
        existingProduct.setCreatedOn(LocalDateTime.now());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("Admin_123");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(productRepository.findById(1))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.save(any(Product.class)))
                .thenReturn(existingProduct);

        // Act
        ProductResponseDto response =
                productService.updateProduct(1, request);

        // Assert
        assertNotNull(response);

        assertEquals(
                "Keyboard",
                response.getProductName()
        );

        assertEquals(
                "Admin_123",
                response.getModifiedBy()
        );

        assertNotNull(response.getModifiedOn());

        verify(productRepository, times(1))
                .findById(1);

        verify(productRepository, times(1))
                .save(existingProduct);
    }


    // ---------------------------------------------------------
    // UPDATE PRODUCT - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void testUpdateProduct_WhenProductDoesNotExist() {

        // Arrange
        ProductRequestDto request = new ProductRequestDto();
        request.setProductName("Keyboard");

        when(productRepository.findById(999))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(999, request)
        );

        verify(productRepository, times(1))
                .findById(999);

        verify(productRepository, never())
                .save(any(Product.class));
    }


    // ---------------------------------------------------------
    // DELETE PRODUCT - SUCCESS
    // ---------------------------------------------------------

    @Test
    void testDeleteProduct_WhenProductExists() {

        // Arrange
        Product product = new Product();
        product.setId(1);
        product.setProductName("Laptop");

        when(productRepository.findById(1))
                .thenReturn(Optional.of(product));

        // Act
        productService.deleteProduct(1);

        // Assert
        verify(productRepository, times(1))
                .findById(1);

        verify(productRepository, times(1))
                .delete(product);
    }


    // ---------------------------------------------------------
    // DELETE PRODUCT - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void testDeleteProduct_WhenProductDoesNotExist() {

        // Arrange
        when(productRepository.findById(999))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(999)
        );

        verify(productRepository, times(1))
                .findById(999);

        verify(productRepository, never())
                .delete(any(Product.class));
    }


    // ---------------------------------------------------------
    // CLEAR SECURITY CONTEXT AFTER EACH TEST
    // ---------------------------------------------------------

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}