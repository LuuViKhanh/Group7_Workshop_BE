package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.NewsArticleDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.List;

@Repository
public class NewsArticleRepository extends AbstractDynamoRepository<NewsArticleDynamoDB> {

    public NewsArticleRepository(DynamoDbEnhancedClient client) {
        super(client, NewsArticleDynamoDB.class, "NewsArticle");
    }

    public List<NewsArticleDynamoDB> findAllByOrderByCreatedAtDesc() {
        List<NewsArticleDynamoDB> list = findAll();
        // Sắp xếp bằng Java vì DynamoDB Scan không đảm bảo thứ tự
        list.sort((a, b) -> {
            if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
            return b.getCreatedAt().compareTo(a.getCreatedAt()); // DESC
        });
        return list;
    }
}