package org.example.flyora_backend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.flyora_backend.DTOs.ProductReviewDTO;
import org.example.flyora_backend.repository.CustomerRepository;
import org.example.flyora_backend.repository.ProductRepository;
import org.example.flyora_backend.repository.ProductReviewRepository;
import org.example.flyora_backend.Utils.IdGeneratorUtil;
import org.example.flyora_backend.dynamo.models.CustomerDynamoDB;
import org.example.flyora_backend.dynamo.models.ProductReviewDynamoDB;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository; 
    private final CustomerRepository customerRepository;
    private final IdGeneratorUtil idGeneratorUtil;

    @Override
    public Map<String, Object> submitReview(ProductReviewDTO dto) {
        // Check tồn tại
        if(productRepository.findById(dto.getProductId()).isEmpty())
             throw new RuntimeException("Không tìm thấy sản phẩm");
        
        if(customerRepository.findById(dto.getCustomerId()).isEmpty())
             throw new RuntimeException("Không tìm thấy khách hàng");

        ProductReviewDynamoDB review = new ProductReviewDynamoDB();
        review.setId(idGeneratorUtil.generateProductReviewId());

        // SỬA LỖI: Set ID thay vì set Object
        review.setCustomerId(dto.getCustomerId());
        review.setProductId(dto.getProductId());
        
        review.setRating(dto.getRating());
        review.setReview(dto.getComment());

        reviewRepository.save(review);

        return Map.of("message", "Đánh giá sản phẩm thành công");
    }

    @Override
    public List<ProductReviewDTO> getReviewsByProduct(Integer productId) {
        // Lấy list review
        List<ProductReviewDynamoDB> reviews = reviewRepository.findByProductId(productId);
        
        return reviews.stream().map(review -> {
            // Manual Join lấy tên Customer
            String customerName = customerRepository.findById(review.getCustomerId())
                    .map(CustomerDynamoDB::getName).orElse("Unknown User");

            return new ProductReviewDTO(
                review.getCustomerId(), // Getter lấy ID
                review.getProductId(),  // Getter lấy ID
                review.getRating(),
                review.getReview(),
                customerName // Tên khách hàng đã tìm
            );
        }).collect(Collectors.toList());
    }
}