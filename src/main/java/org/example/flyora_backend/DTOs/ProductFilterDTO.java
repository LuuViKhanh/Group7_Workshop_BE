package org.example.flyora_backend.DTOs;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductFilterDTO {
    private String name;              // tên chứa từ khoá
    private Integer categoryId;
    private Integer birdTypeId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer page = 0;         // phân trang
    private Integer size = 10;
}
