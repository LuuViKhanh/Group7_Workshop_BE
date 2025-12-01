package org.example.flyora_backend.service;

import java.util.Map;

import org.example.flyora_backend.DTOs.LoginDTO;
import org.example.flyora_backend.DTOs.LoginResponseDTO;
import org.example.flyora_backend.DTOs.RegisterDTO;

public interface AuthService {
    Map<String, Object> registerCustomer(RegisterDTO request);

    LoginResponseDTO loginCustomer(LoginDTO request);
}
