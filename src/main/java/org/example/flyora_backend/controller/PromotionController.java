package org.example.flyora_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.flyora_backend.DTOs.PromotionDTO;
import org.example.flyora_backend.service.PromotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@Tag(name = "Promotions", description = "Danh sách các mã khuyến mãi đang có hiệu lực")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    /**
     * ✅ API lấy danh sách khuyến mãi
     * 🔹 GET /api/v1/promotions?customerId=1
     * 🔸 Trả: danh sách mã khuyến mãi theo khách hàng
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách mã khuyến mãi", description = "Trả về danh sách mã giảm giá cho khách hàng.")
    public ResponseEntity<List<PromotionDTO>> getPromotions(@RequestParam(required = false) Integer customerId) {
        return ResponseEntity.ok(promotionService.getAllPromotions(customerId));
    }
}