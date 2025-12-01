package org.example.flyora_backend.controller;

import java.util.List;

import org.example.flyora_backend.DTOs.NewsArticleResponseDTO;
import org.example.flyora_backend.dynamo.models.FaqDynamoDB;
import org.example.flyora_backend.dynamo.models.PolicyDynamoDB;
import org.example.flyora_backend.service.InfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "News & Blogs", description = "API lấy tin tức, câu hỏi thường gặp, chính sách hệ thống")
public class InfoController {

    private final InfoService infoService;

    @GetMapping("/news")
    @Operation(
        summary = "Lấy danh sách tin tức có kèm ảnh preview",
        description = """
            ✅ Trả về danh sách các bài viết (`NewsArticleResponseDTO`) có thêm ảnh đại diện.

            Mỗi bài viết gồm:
            - id
            - title
            - url
            - createdAt
            - imageUrl ✅
        """
    )
    public ResponseEntity<List<NewsArticleResponseDTO>> getNewsWithImage() {
        return ResponseEntity.ok(infoService.getPublishedNewsWithImage());
    }



    @GetMapping("/faqs")
    @Operation(
        summary = "Lấy danh sách câu hỏi thường gặp (FAQ)",
        description = """
            ✅ Trả về danh sách câu hỏi (`Faq`) được sử dụng phổ biến trong hệ thống.

            Mỗi mục FAQ gồm:
            - id (Integer)
            - question (String)
            - answer (Text)
        """
    )
    public ResponseEntity<List<FaqDynamoDB>> getFaqs() {
        return ResponseEntity.ok(infoService.getFaqs());
    }

    @GetMapping("/policies")
    @Operation(
        summary = "Lấy danh sách chính sách hệ thống",
        description = """
            ✅ Trả về toàn bộ chính sách đang được áp dụng (`Policy`).

            Mỗi chính sách gồm:
            - id (Integer)
            - content (Text)
        """
    )
    public ResponseEntity<List<PolicyDynamoDB>> getPolicies() {
        return ResponseEntity.ok(infoService.getPolicies());
    }
}
