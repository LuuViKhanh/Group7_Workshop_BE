package org.example.flyora_backend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDTO {
    private Integer productId;
    private Integer quantity;
}
