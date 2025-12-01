package org.example.flyora_backend.DTOs;

import java.math.BigDecimal;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDTO {
    private Integer productId;
    private String productName;
    private String imageUrl;
    private int totalSold;
    private BigDecimal price;
}
