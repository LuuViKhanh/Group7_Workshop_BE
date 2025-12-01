package org.example.flyora_backend.DTOs;

import java.math.BigDecimal;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerProductListDTO {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String status;    // VD: "Còn hàng", "Hết hàng"
    private String imageUrl;
}
