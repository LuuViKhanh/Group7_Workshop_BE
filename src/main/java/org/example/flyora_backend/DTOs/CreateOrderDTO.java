package org.example.flyora_backend.DTOs;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {
    private Integer customerId;
    private List<OrderItemDTO> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Integer productId;
        private Integer quantity;
    }
}
