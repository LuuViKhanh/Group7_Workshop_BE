package org.example.flyora_backend.DTOs;

import java.math.BigDecimal;

public record ProductBestSellerDTO(
    Integer productId,
    String productName,
    String categoryName,
    BigDecimal price,
    Integer totalSold,
    String imageUrl
) {}
 