package org.example.flyora_backend.DTOs;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String email;
    private String phone;
    private String name;
}
