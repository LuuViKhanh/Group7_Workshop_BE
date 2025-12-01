package org.example.flyora_backend.repository;

import java.util.Optional;

import org.example.flyora_backend.dynamo.models.FoodDetailDynamoDB;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class FoodDetailRepository extends AbstractDynamoRepository<FoodDetailDynamoDB> {
    public FoodDetailRepository(DynamoDbEnhancedClient client) {
        super(client, FoodDetailDynamoDB.class, "FoodDetail");
    }
    
    // Cần GSI trên cột product_id
    public Optional<FoodDetailDynamoDB> findByProductId(Integer productId) {
         return table.index("product_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(productId)))
                .stream().flatMap(p -> p.items().stream())
                .findFirst();
    }
}
