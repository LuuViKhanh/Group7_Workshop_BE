package org.example.flyora_backend.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Integer id;
    private String content;
    private String sentAt;
}
