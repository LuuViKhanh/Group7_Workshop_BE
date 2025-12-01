package org.example.flyora_backend.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private String username;
    private String password;
    private String email;
    private String phone;

    private Integer isActive;      // true = đã kích hoạt
    private Integer isApproved;    // true = đã được duyệt

    private Integer roleId;        // 1 = ADMIN, 2 = SHOP_OWNER, 3 = SALESSTAFF, 4 = CUSTOMER
    private String roleName;       // (tuỳ chọn, dùng hiển thị hoặc debug)

    private Integer approvedBy;    // ID của Admin duyệt tài khoản

    // Thông tin người dùng tuỳ theo vai trò
    private String name;           // tên người dùng cho Admin / Customer / ShopOwner / Staff

    private String otherInfo;      // (tuỳ chọn) - chỉ dùng cho ShopOwner
    private Integer shopOwnerId;   // (tuỳ chọn) - dùng cho SALESSTAFF để biết nhân viên thuộc chủ shop nào
}