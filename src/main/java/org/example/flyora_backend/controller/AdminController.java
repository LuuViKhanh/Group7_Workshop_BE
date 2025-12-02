package org.example.flyora_backend.controller;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.flyora_backend.DTOs.AccessLogDTO;
import org.example.flyora_backend.DTOs.AccountDTO;
import org.example.flyora_backend.DTOs.CreateNewsDTO;
import org.example.flyora_backend.DTOs.UserDTO;
import org.example.flyora_backend.dynamo.models.AccountDynamoDB;
import org.example.flyora_backend.repository.AccessLogRepository;
import org.example.flyora_backend.repository.AccountRepository;
import org.example.flyora_backend.service.AccessLogService;
import org.example.flyora_backend.service.AccountService;
import org.example.flyora_backend.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/admin/accounts")
@Tag(name = "Admin Services")
public class AdminController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private InfoService infoService;

    private void verifyAdmin(Integer requestAccountId) {
        Optional<AccountDynamoDB> optionalAcc = accountRepository.findById(requestAccountId);
        if (optionalAcc.isPresent()) {
            AccountDynamoDB acc = optionalAcc.get();
            if (acc.getRoleId() != 1) {
                throw new RuntimeException("Access denied");
            }
        }
    }

    @PostMapping
    @Operation(summary = "Tạo tài khoản mới")
    public ResponseEntity<?> createAccount(@RequestBody AccountDTO dto, @RequestParam Integer requesterId) {
        verifyAdmin(requesterId);
        accessLogService.logAction(requesterId, "Tạo tài khoản mới");
        AccountDynamoDB account = accountService.createAccount(dto);
        return ResponseEntity.ok(new UserDTO(account));
    }

    @GetMapping
    @Operation(summary = "Xem danh sách tài khoản")
    public ResponseEntity<?> getAllAccounts(@RequestParam Integer requesterId) {
        verifyAdmin(requesterId);
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật tài khoản")
    public ResponseEntity<?> updateAccount(@PathVariable Integer id, @RequestBody AccountDTO dto,
                                           @RequestParam Integer requesterId) {
        verifyAdmin(requesterId);
        accessLogService.logAction(requesterId, "Cập nhật tài khoản");
        AccountDynamoDB account = accountService.updateAccount(id, dto);
        return ResponseEntity.ok(new UserDTO(account));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa tài khoản")
    public ResponseEntity<?> deleteAccount(@PathVariable Integer id, @RequestParam Integer requesterId) {
        verifyAdmin(requesterId);
        accountService.deleteAccount(id);
        accessLogService.logAction(requesterId, "Xóa tài khoản");
        return ResponseEntity.ok("Tài khoản đã được xóa thành công.");
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateAccount(@PathVariable Integer id, @RequestParam Integer requesterId) {
        verifyAdmin(requesterId);
        accessLogService.logAction(requesterId, "Kích hoạt tài khoản");
        AccountDynamoDB account = accountService.setActiveStatus(id, 1);
        return ResponseEntity.ok(new UserDTO(account));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateAccount(@PathVariable Integer id, @RequestParam Integer requesterId) {
        verifyAdmin(requesterId);
        accessLogService.logAction(requesterId, "Hủy kích hoạt tài khoản");
        AccountDynamoDB account = accountService.setActiveStatus(id, 0);
        return ResponseEntity.ok(new UserDTO(account));
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getAccessLogs(@RequestParam Integer requesterId) {
        verifyAdmin(requesterId);
        
        List<AccessLogDTO> logs = accessLogRepository.findAll().stream()
                .map(log -> {
                    AccessLogDTO dto = new AccessLogDTO();
                    dto.setAccountId(log.getAccountId());

                    accountRepository.findById(log.getAccountId())
                            .ifPresent(acc -> dto.setUsername(acc.getUsername()));

                    dto.setAction(log.getAction());

                    String ts = log.getTimestamp();
                    // 1. Thử parse ISO-8601 (2025-07-05T15:43:19Z)
                    try {
                        dto.setTimestamp(
                                Instant.parse(ts)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime()
                        );
                    }
                    catch (Exception e1) {
                        // 2. Thử parse SQL datetime (2025-07-05 15:43:19)
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            dto.setTimestamp(LocalDateTime.parse(ts, formatter));
                        }
                        catch (Exception e2) {
                            // 3. Thử parse Epoch milliseconds (1712345678901)
                            try {
                                dto.setTimestamp(
                                        LocalDateTime.ofInstant(
                                                Instant.ofEpochMilli(Long.parseLong(ts)),
                                                ZoneId.systemDefault()
                                        )
                                );
                            }
                            catch (Exception e3) {
                                // 4. Bó tay → báo lỗi chính xác
                                throw new RuntimeException("Invalid timestamp format: " + ts);
                            }
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }


    @PostMapping("/news")
    public ResponseEntity<?> createNews(@RequestBody CreateNewsDTO dto, @RequestParam Integer requesterId) {
        verifyAdmin(requesterId);
        accessLogService.logAction(requesterId, "Tạo bản tin: " + dto.getTitle());
        return ResponseEntity.ok(infoService.createNewsArticle(dto));
    }
}