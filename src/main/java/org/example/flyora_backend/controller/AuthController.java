package org.example.flyora_backend.controller;

import org.example.flyora_backend.DTOs.LoginDTO;
import org.example.flyora_backend.DTOs.RegisterDTO;
import org.example.flyora_backend.service.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Login & Register", description = "Đăng ký và đăng nhập cho khách hàng")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/register")
    @Operation(
        summary = "Đăng ký tài khoản khách hàng",
        description = """
            Tạo tài khoản khách hàng mới.

            ✅ Trường yêu cầu trong body (RegisterDTO):
            - username (String)
            - password (String)
            - email (String)
            - phone (String)
            - name (String)

            🔁 Trả về: message và userId nếu thành công.
        """
    )
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody RegisterDTO request) {
        return ResponseEntity.ok(authService.registerCustomer(request));
    }
    
    @PostMapping("/login")
    @Operation(
        summary = "Đăng nhập tài khoản khách hàng",
        description = """
            Đăng nhập hệ thống với tài khoản khách hàng.

            ✅ Trường yêu cầu trong body (LoginDTO):
            - username (String)
            - password (String)

            🔁 Trả về: userId, name, role, token nếu đăng nhập thành công.
        """
    )
    public ResponseEntity<?> loginCustomer(@Valid @RequestBody LoginDTO request) {        
        return ResponseEntity.ok(authService.loginCustomer(request));
    }
}
