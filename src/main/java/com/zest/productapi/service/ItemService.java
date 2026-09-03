package com.zest.productapi.service;

import com.zest.productapi.dto.ItemResponseDto;
import com.zest.productapi.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.zest.productapi.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<ItemResponseDto> getItemsByProductId(Integer productId) {
        List<Item> items = itemRepository.findByProductId(productId);
        List<ItemResponseDto> responses = new ArrayList<>();
        for (Item item : items) {
            responses.add(mapToResponse(item));
        }
        return responses;
    }
    private ItemResponseDto mapToResponse(Item item) {
        ItemResponseDto response = new ItemResponseDto();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setQuantity(item.getQuantity());

        return response;
    }
}