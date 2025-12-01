package org.example.flyora_backend.controller;

import java.util.Map;
import org.example.flyora_backend.DTOs.ProductReviewDTO;
import org.example.flyora_backend.service.AccessLogService;
import org.example.flyora_backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Product Review", description = "Gửi đánh giá cho sản phẩm")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    private AccessLogService accessLogService;

    @PostMapping("/submit")
    @Operation(
        summary = "Gửi đánh giá sản phẩm",
        description = """
            Gửi đánh giá và bình luận của khách hàng về một sản phẩm đã mua.

            ✅ Trường yêu cầu trong body (ProductReviewDTO):
            - customerId (Integer): ID của khách hàng
            - productId (Integer): ID của sản phẩm
            - rating (Integer): điểm đánh giá từ 1 đến 5
            - comment (String): nội dung đánh giá (tối đa 500 ký tự, có thể để trống)

            🔁 Trả về: Thông báo thành công hoặc lỗi nếu có vấn đề xảy ra.
        """
    )
    public ResponseEntity<?> submitReview(@Valid @RequestBody ProductReviewDTO request) {
        try {
            accessLogService.logAction(request.getCustomerId(), "Gửi đánh giá sản phẩm");
            return ResponseEntity.ok(reviewService.submitReview(request));
        } catch (Exception e) {
            return ResponseEntity
                .status(500)
                .body(Map.of("error", "Đánh giá thất bại: " + e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    @Operation(
        summary = "Xem đánh giá sản phẩm",
        description = """
            Trả về danh sách tất cả đánh giá của khách hàng cho một sản phẩm cụ thể.

            ✅ Đường dẫn:
            - productId (Integer): ID của sản phẩm cần lấy đánh giá

            🔁 Trả về: Danh sách các đánh giá gồm thông tin customerId, productId, rating, comment.
        """
    )
    public ResponseEntity<?> getReviewsForProduct(@PathVariable Integer productId) {
        try {
            return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
        } catch (Exception e) {
            return ResponseEntity
                .status(500)
                .body(Map.of("error", "Không thể lấy đánh giá: " + e.getMessage()));
        }
    }
}