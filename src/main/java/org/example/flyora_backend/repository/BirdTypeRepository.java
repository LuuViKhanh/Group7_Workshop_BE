package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.BirdTypeDynamoDB;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Repository
public class BirdTypeRepository extends AbstractDynamoRepository<BirdTypeDynamoDB>{
    public BirdTypeRepository(DynamoDbEnhancedClient client) {
        super(client, BirdTypeDynamoDB.class, "BirdType");
    }
}
