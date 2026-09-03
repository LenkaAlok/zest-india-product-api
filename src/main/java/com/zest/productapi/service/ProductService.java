package com.zest.productapi.service;
import com.zest.productapi.dto.ProductRequestDto;
import com.zest.productapi.dto.ProductResponseDto;
import com.zest.productapi.entity.Product;
import com.zest.productapi.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.zest.productapi.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // CREATE
    public ProductResponseDto createProduct(
            ProductRequestDto productRequestDto) {

        Product product = new Product();

        product.setProductName(productRequestDto.getProductName());

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        product.setCreatedBy(username);
        product.setCreatedOn(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    // GET ALL
    public List<ProductResponseDto> getAllProducts() {

        List<Product> products = productRepository.findAll();

        List<ProductResponseDto> responses = new ArrayList<>();

        for (Product product : products) {
            responses.add(mapToResponse(product));
        }

        return responses;
    }

    // GET BY ID
    public ProductResponseDto getProductById(Integer id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id));

        return mapToResponse(product);
    }

    // UPDATE
    public ProductResponseDto updateProduct(
            Integer id,
            ProductRequestDto productRequestDto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id));

        product.setProductName(productRequestDto.getProductName());

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        product.setModifiedBy(username);
        product.setModifiedOn(LocalDateTime.now());
        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    // DELETE
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id));
        productRepository.delete(product);
    }

    // ENTITY → RESPONSE DTO
    private ProductResponseDto mapToResponse(Product product) {
        ProductResponseDto response = new ProductResponseDto();
        response.setId(product.getId());
        response.setProductName(product.getProductName());
        response.setCreatedBy(product.getCreatedBy());
        response.setCreatedOn(product.getCreatedOn());
        response.setModifiedBy(product.getModifiedBy());
        response.setModifiedOn(product.getModifiedOn());

        return response;
    }
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {

        Page<Product> products = productRepository.findAll(pageable);

        return products.map(this::mapToResponse);
    }
}