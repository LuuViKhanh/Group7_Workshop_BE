package org.example.flyora_backend.DTOs;

import lombok.Data;

@Data
public class ProfileDTO {
    private Integer accountId;
    private String username;
    private String email;
    private String phone;
    private String roleName;
    private String name; // tên của người dùng tương ứng theo vai trò
}
