package org.example.flyora_backend.DTOs;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String name; // tên hiển thị của customer
}
