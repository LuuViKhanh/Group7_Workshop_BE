package org.example.flyora_backend.service;

import org.example.flyora_backend.dynamo.models.NewsArticleDynamoDB;
import org.example.flyora_backend.repository.NewsArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsArticleService {

    @Autowired
    private NewsArticleRepository newsArticleRepository;

    public List<NewsArticleDynamoDB> getAllArticles() {
        return newsArticleRepository.findAll();
    }

    public Optional<NewsArticleDynamoDB> getOneArticle(Integer id) {
        return newsArticleRepository.findById(id);
    }

    public NewsArticleDynamoDB addArticle(NewsArticleDynamoDB article) {
        // SỬA LỖI: Save xong trả về object
        newsArticleRepository.save(article);
        return article;
    }

    public void deleteArticle(Integer id) {
        // SỬA LỖI: repo method là deleteById
        newsArticleRepository.deleteById(id);
    }
}