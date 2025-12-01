package org.example.flyora_backend.DTOs;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
