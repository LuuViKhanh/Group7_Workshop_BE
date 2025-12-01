package org.example.flyora_backend.service;

import java.util.List;
import org.example.flyora_backend.DTOs.PromotionDTO;

public interface PromotionService {
    List<PromotionDTO> getAllPromotions(Integer customerId);
}