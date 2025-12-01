package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.ShippingMethodDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Repository
public class ShippingMethodRepository extends AbstractDynamoRepository<ShippingMethodDynamoDB> {
    public ShippingMethodRepository(DynamoDbEnhancedClient client) {
        super(client, ShippingMethodDynamoDB.class, "ShippingMethod");
    }
}