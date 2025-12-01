package org.example.flyora_backend.controller;

import java.util.List;
import java.util.Map;

import org.example.flyora_backend.DTOs.CalculateFeeRequestDTO;
import org.example.flyora_backend.DTOs.CreateOrderRequestDTO;
import org.example.flyora_backend.DTOs.DistrictDTO;
import org.example.flyora_backend.DTOs.ProvinceDTO;
import org.example.flyora_backend.DTOs.WardDTO;
import org.example.flyora_backend.dynamo.models.AccountDynamoDB;
import org.example.flyora_backend.dynamo.models.DeliveryNoteDynamoDB;
import org.example.flyora_backend.dynamo.models.OrderDynamoDB;
import org.example.flyora_backend.repository.AccountRepository;
import org.example.flyora_backend.repository.DeliveryNoteRepository;
import org.example.flyora_backend.repository.OrderRepository;
import org.example.flyora_backend.service.GHNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/shipping-utils")
@Tag(name = "Shipping Utilities (GHN)", description = "Các API tiện ích GHN")
public class GHNController {

    @Autowired
    private GHNService ghnService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DeliveryNoteRepository deliveryNoteRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Xác thực tài khoản dựa vào requesterId
     * Dùng với DynamoDB model mới (Boolean đã thành Integer)
     */
    private AccountDynamoDB verifyAccess(Integer requesterId) {
        AccountDynamoDB acc = accountRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        // ✔ Kiểm tra Integer thay cho Boolean
        if (acc.getIsActive() != 1 || acc.getIsApproved() != 1 ) {
            throw new RuntimeException("Tài khoản bị khóa hoặc chưa được duyệt");
        }

        return acc;
    }

    @GetMapping("/provinces")
    public ResponseEntity<List<ProvinceDTO>> getProvinces(@RequestParam Integer requesterId) {
        verifyAccess(requesterId);
        return ResponseEntity.ok(ghnService.getProvinces());
    }

    @GetMapping("/districts")
    public ResponseEntity<List<DistrictDTO>> getDistricts(
            @RequestParam Integer requesterId,
            @RequestParam int provinceId) {
        verifyAccess(requesterId);
        return ResponseEntity.ok(ghnService.getDistricts(provinceId));
    }

    @GetMapping("/wards")
    public ResponseEntity<List<WardDTO>> getWards(
            @RequestParam Integer requesterId,
            @RequestParam int districtId) {
        verifyAccess(requesterId);
        return ResponseEntity.ok(ghnService.getWard(districtId));
    }

    @PostMapping("/calculate-fee")
    public ResponseEntity<Map<String, Object>> calculateShippingFee(
            @RequestParam Integer requesterId,
            @RequestBody CalculateFeeRequestDTO feeRequest) {
        verifyAccess(requesterId);
        return ResponseEntity.ok(ghnService.calculateFee(feeRequest));
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createShippingOrder(
            @RequestParam Integer requesterId,
            @RequestBody CreateOrderRequestDTO orderRequest) {

        verifyAccess(requesterId);
        Map<String, Object> ghnResponse = ghnService.createOrder(orderRequest);

        return ResponseEntity.ok(ghnResponse);
    }

    /**
     * Theo dõi trạng thái đơn hàng – phiên bản DynamoDB
     */
    @GetMapping("/track")
    public ResponseEntity<?> trackOrder(
            @RequestParam Integer requesterId,
            @RequestParam String orderCode) {

        AccountDynamoDB account = verifyAccess(requesterId);

        // 1️⃣ Tìm delivery note theo tracking number
        DeliveryNoteDynamoDB deliveryNote = deliveryNoteRepository.findByTrackingNumber(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với mã vận đơn này."));

        // 2️⃣ Tìm order bằng orderId từ delivery note
        OrderDynamoDB order = orderRepository.findById(deliveryNote.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Order tương ứng với DeliveryNote."));

        Integer customerId = order.getCustomerId();  // ✔ Model Order bắt buộc phải có trường này

        // 3️⃣ Kiểm tra quyền truy cập
        boolean isOwner = customerId.equals(requesterId);

        boolean isAdminOrOwner = 
                account.getRoleId() == 1 ||
                account.getRoleId() == 2 ||
                account.getRoleId() == 3;

        if (!isOwner && !isAdminOrOwner) {
                return ResponseEntity.status(403).body(
                Map.of("error", "Bạn không có quyền xem đơn hàng này.")
                );
        }

        // 4️⃣ Lấy thông tin từ GHN
        Map<String, Object> orderDetails = ghnService.getOrderStatus(orderCode);

        return ResponseEntity.ok(orderDetails);
    }
}
