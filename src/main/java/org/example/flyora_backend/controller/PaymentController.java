// package org.example.flyora_backend.controller;

// import org.example.flyora_backend.model.Order;
// import org.example.flyora_backend.repository.OrderRepository;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/payment")
// public class PaymentController {

//     private final OrderRepository orderRepository;

//     @PostMapping("/cancel/{orderCode}")
//     public ResponseEntity<String> cancelOrder(@PathVariable String orderCode) {
//         Order order = orderRepository.findByOrderCode(orderCode)
//                 .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

//         if (!"PAID".equalsIgnoreCase(order.getStatus())) {
//             order.setStatus("CANCELLED");
//             orderRepository.save(order);
//             return ResponseEntity.ok("Đã hủy đơn hàng: " + orderCode);
//         } else {
//             return ResponseEntity.badRequest().body("Không thể hủy. Đơn hàng đã thanh toán.");
//         }
//     }
// }
