package org.example.flyora_backend.DTOs;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductListDTO {
    private Integer id;
    private String name;
    private String category;
    private String birdType;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
}