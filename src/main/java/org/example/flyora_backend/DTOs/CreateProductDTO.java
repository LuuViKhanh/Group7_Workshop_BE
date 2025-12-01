package org.example.flyora_backend.DTOs;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreateProductDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer categoryId;
    private Integer birdTypeId;

    // Thông tin chi tiết tùy theo loại
    private String material;
    private String origin;
    private String usageTarget;     // chỉ FOODS
    private BigDecimal weight;
    private String color;           // TOYS/FURNITURE
    private String dimensions;      // TOYS/FURNITURE
    private String imageUrl;
}
