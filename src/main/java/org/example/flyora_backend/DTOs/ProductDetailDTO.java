package org.example.flyora_backend.DTOs;

import java.math.BigDecimal;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDTO {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private String birdType;
    private String imageUrl;
}