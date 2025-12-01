package org.example.flyora_backend.service;

import java.util.List;
import org.example.flyora_backend.DTOs.NotificationDTO;

public interface NotificationService {
    List<NotificationDTO> getNotifications(Integer recipientId);
}