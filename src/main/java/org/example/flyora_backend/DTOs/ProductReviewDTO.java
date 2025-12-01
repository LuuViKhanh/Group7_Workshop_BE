package org.example.flyora_backend.DTOs;

import javax.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductReviewDTO {

    @NotNull
    private Integer customerId;

    @NotNull
    private Integer productId;

    @Min(1)
    @Max(5)
    private int rating;

    @Size(max = 500)
    private String comment;

    private String customerName;

    /**
     * Constructor này chỉ dùng khi gửi review lên, không cần customerName.
     */
    public ProductReviewDTO(Integer customerId, Integer productId, int rating, String comment) {
        this.customerId = customerId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
    }

}
