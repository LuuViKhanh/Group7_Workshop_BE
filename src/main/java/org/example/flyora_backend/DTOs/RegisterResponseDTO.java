package org.example.flyora_backend.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
    private String message;
    private String username;
}
