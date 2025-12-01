package org.example.flyora_backend.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewsDTO {
    private String url;       // Link tới bài viết gốc (sẽ được crawl)
    private String title;     // Tiêu đề hiển thị
}
