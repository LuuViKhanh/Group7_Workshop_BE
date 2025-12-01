package org.example.flyora_backend.controller;

import org.example.flyora_backend.DTOs.ChangePasswordDTO;
import org.example.flyora_backend.DTOs.ProfileDTO;
import org.example.flyora_backend.DTOs.UpdateProfileDTO;
import org.example.flyora_backend.service.AccessLogService;
import org.example.flyora_backend.service.ProfileService;
import org.example.flyora_backend.Utils.JwtUtil;
import org.example.flyora_backend.dynamo.models.AccountDynamoDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Account's Profile")
@RequiredArgsConstructor
public class ProfileController {
    private final JwtUtil jwtUtil;
    private final ProfileService profileService;

    @Autowired
    private AccessLogService accessLogService;

    @GetMapping
    @Operation(
        summary = "Xem hồ sơ người dùng",
        description = """
            Trả về thông tin hồ sơ người dùng từ token.

            📌 Header cần có: `Authorization: Bearer {token}`

            🔁 Trả về: ProfileDTO gồm thông tin cơ bản của người dùng.
        """
    )
    public ResponseEntity<ProfileDTO> getProfile(@RequestHeader("Authorization") String token) {
        AccountDynamoDB account = jwtUtil.getAccountFromToken(token);
        accessLogService.logAction(account.getId(), "Xem hồ sơ người dùng");
        return ResponseEntity.ok(profileService.getProfile(account));
    }

    
    @PutMapping
    @Operation(
        summary = "Cập nhật hồ sơ người dùng",
        description = """
            Cập nhật thông tin hồ sơ như tên, email, số điện thoại (tùy loại tài khoản).

            📌 Header: `Authorization: Bearer {token}`
            📌 Body: UpdateProfileDTO

            🔁 Trả về: 200 OK nếu thành công.
        """
    )
    public ResponseEntity<Void> updateProfile(@RequestHeader("Authorization") String token,
                                              @RequestBody UpdateProfileDTO request) {
        AccountDynamoDB account = jwtUtil.getAccountFromToken(token);
        profileService.updateProfile(account, request);
        accessLogService.logAction(account.getId(), "Cập nhật hồ sơ người dùng");
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    @Operation(
        summary = "Thay đổi mật khẩu",
        description = """
            Thay đổi mật khẩu cho tài khoản hiện tại.

            📌 Header: `Authorization: Bearer {token}`
            📌 Body: ChangePasswordDTO gồm oldPassword và newPassword

            🔁 Trả về: 200 OK nếu thay đổi thành công.
        """
    )
    public ResponseEntity<Void> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordDTO request) {


        AccountDynamoDB account = jwtUtil.getAccountFromToken(token);
        profileService.changePassword(account, request);
        accessLogService.logAction(account.getId(), "Thay đổi mật khẩu");
        return ResponseEntity.ok().build();
    }

}
