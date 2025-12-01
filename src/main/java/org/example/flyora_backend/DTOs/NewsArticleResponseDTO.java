package org.example.flyora_backend.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleResponseDTO {
    private Integer id;
    private String title;
    private String url;
    private String createdAt;
    private String imageUrl;
}