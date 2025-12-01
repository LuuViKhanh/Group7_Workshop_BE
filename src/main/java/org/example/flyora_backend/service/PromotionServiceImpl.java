package org.example.flyora_backend.service;

import lombok.RequiredArgsConstructor;
import org.example.flyora_backend.DTOs.PromotionDTO;
import org.example.flyora_backend.dynamo.models.PromotionDynamoDB; // SỬA LỖI IMPORT
import org.example.flyora_backend.repository.PromotionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public List<PromotionDTO> getAllPromotions(Integer customerId) {
        // SỬA LỖI: method findByCustomerId (không có underscore)
        List<PromotionDynamoDB> promotions = (customerId != null)
            ? promotionRepository.findByCustomerId(customerId)
            : promotionRepository.findAll();

        return promotions.stream()
            .map(p -> new PromotionDTO(
                p.getId(),
                p.getCode(),
                p.getDiscount(),
                p.getProductId(), // Lấy ID trực tiếp
                p.getCustomerId() // Lấy ID trực tiếp
            ))
            .collect(Collectors.toList());
    }
}