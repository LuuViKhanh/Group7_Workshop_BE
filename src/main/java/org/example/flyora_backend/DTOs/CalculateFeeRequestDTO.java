package org.example.flyora_backend.DTOs;

import com.fasterxml.jackson.annotation.*;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CalculateFeeRequestDTO {
    // --- Thông tin địa chỉ người nhận ---
    @JsonProperty("to_district_id")
    private int to_district_id;

    @JsonProperty("to_ward_code")
    private String to_ward_code; // Phải là String theo đúng yêu cầu của GHN

    // --- Thông tin gói hàng (đơn vị: gram và cm) ---
    private int weight;
    private int height;
    private int length;
    private int width;

    // --- Thông tin giá trị đơn hàng (để tính phí bảo hiểm) ---
    @JsonProperty("insurance_value")
    private int insurance_value;

    /**
     * ID Dịch vụ của GHN. Frontend có thể cho người dùng chọn hoặc mặc định.
     * ID thông dụng:
     * - 53320: Giao nhanh (Express)
     * - 53321: Giao tiết kiệm (Standard)
     */
    @JsonProperty("service_id")
    private int service_id;
}