package org.example.flyora_backend.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueReportDTO {
    private Integer customerId;
    private Integer orderId;
    private String content;
}
