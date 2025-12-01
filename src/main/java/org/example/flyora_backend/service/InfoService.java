package org.example.flyora_backend.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.example.flyora_backend.DTOs.CreateNewsDTO;
import org.example.flyora_backend.DTOs.NewsArticleResponseDTO;
import org.example.flyora_backend.dynamo.models.FaqDynamoDB;
import org.example.flyora_backend.dynamo.models.NewsArticleDynamoDB;
import org.example.flyora_backend.dynamo.models.PolicyDynamoDB;
import org.example.flyora_backend.repository.FaqRepository;
import org.example.flyora_backend.repository.NewsArticleRepository;
import org.example.flyora_backend.repository.PolicyRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final FaqRepository faqRepository;
    private final PolicyRepository policyRepository;
    private final NewsArticleRepository newsArticleRepository;

    public List<FaqDynamoDB> getFaqs() {
        return faqRepository.findAll();
    }

    public List<PolicyDynamoDB> getPolicies() {
        return policyRepository.findAll();
    }

    public List<NewsArticleResponseDTO> getPublishedNewsWithImage() {
        return newsArticleRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(article -> {
                String imageUrl = fetchPreviewImage(article.getUrl());
                return new NewsArticleResponseDTO(
                    article.getId(),
                    article.getTitle(),
                    article.getUrl(),
                    article.getCreatedAt(), // Đã là String
                    imageUrl
                );
            })
            .collect(Collectors.toList());
    }

    private String fetchPreviewImage(String url) {
        try {
            Document doc = Jsoup.connect(url).timeout(5000).get();
            return doc.select("meta[property=og:image]").attr("content");
        } catch (Exception e) {
            return "https://via.placeholder.com/300x200?text=No+Image";
        }
    }

    public NewsArticleDynamoDB createNewsArticle(CreateNewsDTO dto) {
        NewsArticleDynamoDB article = new NewsArticleDynamoDB();
        // Cần generate ID (giả sử AbstractRepo có hàm generateNewId)
        article.setId(newsArticleRepository.generateNewId()); 
        article.setTitle(dto.getTitle());
        article.setUrl(dto.getUrl());
        // SỬA LỖI: Convert Instant sang String
        article.setCreatedAt(Instant.now().toString());
        
        newsArticleRepository.save(article);
        
        // SỬA LỖI: Trả về object sau khi save
        return article;
    }
}