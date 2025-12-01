package org.example.flyora_backend.DTOs;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private Integer userId;     // ID trong bảng Account
    private String name;        // Tên hiển thị (theo role)
    private String role;        // CUSTOMER / ADMIN / SHOP_OWNER / STAFF
    private String token;       // Token (nếu có)
    private Integer linkedId;   // ID trong bảng Customer / ShopOwner / Admin / SalesStaff
}
