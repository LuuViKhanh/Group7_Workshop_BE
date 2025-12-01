package org.example.flyora_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.example.flyora_backend.DTOs.CartItemDTO;
import org.example.flyora_backend.DTOs.CartRequestDTO;
import org.example.flyora_backend.dynamo.models.*;
import org.example.flyora_backend.repository.FoodDetailRepository;
import org.example.flyora_backend.repository.FurnitureDetailRepository;
import org.example.flyora_backend.repository.ProductRepository;
import org.example.flyora_backend.repository.ToyDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired private ProductRepository productRepository;
    @Autowired private FoodDetailRepository foodDetailRepository;
    @Autowired private ToyDetailRepository toyDetailRepository;
    @Autowired private FurnitureDetailRepository furnitureDetailRepository;

    public List<CartItemDTO> getCartItems(List<CartRequestDTO> cartRequests) {
        List<CartItemDTO> result = new ArrayList<>();

        for (CartRequestDTO request : cartRequests) {
            ProductDynamoDB product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            String imageUrl = getImageUrlByCategory(product);
            result.add(CartItemDTO.builder()
                    .productId(product.getId())
                    .name(product.getName())
                    .imageUrl(imageUrl)
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .build());
        }

        return result;
    }

    public CartItemDTO updateItem(Integer productId, Integer quantity) {
        ProductDynamoDB product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        String imageUrl = getImageUrlByCategory(product);
        return CartItemDTO.builder()
                .productId(product.getId())
                .name(product.getName())
                .imageUrl(imageUrl)
                .quantity(quantity)
                .price(product.getPrice())
                .build();
    }

    private String getImageUrlByCategory(ProductDynamoDB product) {
        // SỬA LỖI: Dùng getCategoryId() thay vì getCategory().getId()
        Integer categoryId = product.getCategoryId();
        
        // Lưu ý: Logic này giả định categoryId 1=Food, 2=Toy, 3=Furniture.
        // Tốt hơn nên check theo tên Category nếu có thể, nhưng tạm thời giữ ID như logic cũ
        return switch (categoryId) {
            case 1 -> foodDetailRepository.findByProductId(product.getId())
                    .map(FoodDetailDynamoDB::getImageUrl).orElse(null);
            case 2 -> toyDetailRepository.findByProductId(product.getId())
                    .map(ToyDetailDynamoDB::getImageUrl).orElse(null);
            case 3 -> furnitureDetailRepository.findByProductId(product.getId())
                    .map(FurnitureDetailDynamoDB::getImageUrl).orElse(null);
            default -> null;
        };
    }
}