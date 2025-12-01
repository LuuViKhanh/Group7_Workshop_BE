package org.example.flyora_backend.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ItemDTO {
    // Các trường này phải khớp với yêu cầu của GHN
    private String name;
    private int quantity;
    private int price; // Giá của 1 sản phẩm
}