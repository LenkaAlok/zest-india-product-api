package com.zest.productapi.controller;

import com.zest.productapi.dto.ItemResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.zest.productapi.service.ItemService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{productId}/items")
    public ResponseEntity<List<ItemResponseDto>> getItemsByProductId(
            @PathVariable Integer productId) {

        return ResponseEntity.ok(
                itemService.getItemsByProductId(productId)
        );
    }
}