package org.example.flyora_backend.DTOs;

import java.math.BigDecimal;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}