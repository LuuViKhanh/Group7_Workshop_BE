package org.example.flyora_backend.service;

import java.util.List;
import java.util.Map;
import org.example.flyora_backend.DTOs.CreateOrderDTO;
import org.example.flyora_backend.DTOs.CreatePaymentDTO;
import org.example.flyora_backend.DTOs.OrderHistoryDTO;
import org.example.flyora_backend.dynamo.models.OrderDynamoDB;

public interface OrderService {
    Map<String, Object> createOrder(CreateOrderDTO dto);
    Map<String, Object> createPayment(CreatePaymentDTO dto);
    List<OrderHistoryDTO> getOrdersByCustomer(Integer customerId);
    void attachOrderCode(Integer orderId, String orderCode);

    OrderDynamoDB getOrderByCode(String orderCode);

    void save(OrderDynamoDB order);

}
