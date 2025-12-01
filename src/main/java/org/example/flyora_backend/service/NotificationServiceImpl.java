package org.example.flyora_backend.service;

import lombok.RequiredArgsConstructor;
import org.example.flyora_backend.DTOs.NotificationDTO;
import org.example.flyora_backend.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationDTO> getNotifications(Integer recipientId) {        
        // SỬA LỖI: Tên hàm trong Dynamo Repo viết camelCase (không có dấu gạch dưới)
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId)
            .stream()
            .map(n -> new NotificationDTO(
                n.getId(),
                n.getContent(),
                n.getCreatedAt() // Đã là String
            ))
            .collect(Collectors.toList());
    }
}