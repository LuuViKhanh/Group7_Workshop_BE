package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.PromotionDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PromotionRepository extends AbstractDynamoRepository<PromotionDynamoDB> {

    public PromotionRepository(DynamoDbEnhancedClient client) {
        super(client, PromotionDynamoDB.class, "Promotion");
    }

    public List<PromotionDynamoDB> findByCustomerId(Integer customerId) {
        return table.index("customer_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(customerId)))
                .stream().flatMap(p -> p.items().stream()).collect(Collectors.toList());
    }

    public void deleteAllByProductId(Integer productId) {
        List<PromotionDynamoDB> list = table.index("product_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(productId)))
                .stream().flatMap(p -> p.items().stream()).collect(Collectors.toList());
        for(PromotionDynamoDB p : list) deleteById(p.getId());
    }

    public void deleteByCustomerId(Integer customerId) {
        List<PromotionDynamoDB> list = findByCustomerId(customerId);
        for(PromotionDynamoDB p : list) deleteById(p.getId());
    }
}