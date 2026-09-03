package com.zest.productapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemResponseDto {
    private Integer id;
    private Integer productId;
    private Integer quantity;
}
