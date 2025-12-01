package org.example.flyora_backend.DTOs;

import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessLogDTO {
    private Integer accountId;
    private String username;
    private String action;
    private LocalDateTime timestamp;
}
