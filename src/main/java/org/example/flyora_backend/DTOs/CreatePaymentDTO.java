package org.example.flyora_backend.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentDTO {
    private Integer orderId;
    private Integer customerId;
    private Integer paymentMethodId; // 1 = VNPay, 2 = COD
    private Integer amount;          // Chỉ dùng nếu thanh toán VNPay
    private String bankCode;

    private String to_name;
    private String to_phone;
    private String to_address;
    private String to_ward_code;
    private Integer to_district_id;
}
