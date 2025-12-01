package org.example.flyora_backend.service;

import java.time.Instant;
import org.example.flyora_backend.dynamo.models.AccessLogDynamoDB;
import org.example.flyora_backend.repository.AccessLogRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessLogService {
    private final AccessLogRepository accessLogRepository;

    public void logAction(Integer accountId, String action) {
        // Trong DynamoDB, tạo log rất nhanh, không cần check account tồn tại để tiết kiệm chi phí đọc
        AccessLogDynamoDB log = new AccessLogDynamoDB();
        log.setId(accessLogRepository.generateNewId()); // Dùng hàm có sẵn trong AbstractRepo
        log.setAccountId(accountId);
        log.setAction(action);
        log.setTimestamp(Instant.now().toString()); // Lưu String cho chuẩn DynamoDB

        accessLogRepository.save(log);
    }
}