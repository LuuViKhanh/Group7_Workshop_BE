package org.example.flyora_backend.DTOs;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Không hiển thị các trường null
public class SystemLogDTO {
    
    private Integer logId;
    private Integer adminId;
    private String adminName; // Thêm tên admin để dễ hiểu hơn
    private String action;
    private Instant createdAt;

    // Constructor để tạo DTO từ Entity
    public SystemLogDTO(Integer adminId, String action) {
        this.adminId = adminId;
        this.action = action;
    }
}