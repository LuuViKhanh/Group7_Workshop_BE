package org.example.flyora_backend.DTOs;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {
    private Integer id;
    private String code;
    private BigDecimal discount;
    private Integer productId;
    private Integer customerId;
}