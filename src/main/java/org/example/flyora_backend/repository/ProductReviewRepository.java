package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.ProductReviewDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductReviewRepository extends AbstractDynamoRepository<ProductReviewDynamoDB> {

    public ProductReviewRepository(DynamoDbEnhancedClient client) {
        super(client, ProductReviewDynamoDB.class, "ProductReview");
    }

    public List<ProductReviewDynamoDB> findByProductId(Integer productId) {
        return table.index("product_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(productId)))
                .stream().flatMap(p -> p.items().stream())
                .collect(Collectors.toList());
    }

    public void deleteAllByProductId(Integer productId) {
        List<ProductReviewDynamoDB> list = findByProductId(productId);
        for (ProductReviewDynamoDB item : list) deleteById(item.getId());
    }

    // DynamoDB không có "JOIN FETCH", Service phải tự gọi CustomerRepo để lấy info
    public List<ProductReviewDynamoDB> findByProductIdWithCustomer(Integer productId) {
        return findByProductId(productId); 
    }

    public void deleteByCustomerId(Integer customerId) {
        List<ProductReviewDynamoDB> list = table.index("customer_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(customerId)))
                .stream().flatMap(p -> p.items().stream()).collect(Collectors.toList());
        for (ProductReviewDynamoDB item : list) deleteById(item.getId());
    }
}