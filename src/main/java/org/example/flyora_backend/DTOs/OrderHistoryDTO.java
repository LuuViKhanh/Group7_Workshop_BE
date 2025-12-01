package org.example.flyora_backend.DTOs;

import lombok.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryDTO {
    private Integer orderId;
    private String orderCode;
    private Timestamp orderDate;
    private String status;
    private List<OrderDetailDTO> items;
    

    public OrderHistoryDTO(Integer orderId, Timestamp orderDate, String status, String orderCode,
            List<OrderDetailDTO> items) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
        this.orderCode = orderCode;
        this.items = items;
    }
}
