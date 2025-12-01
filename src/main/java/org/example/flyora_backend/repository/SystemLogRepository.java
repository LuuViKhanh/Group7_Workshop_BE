package org.example.flyora_backend.repository;

import java.util.List;
import org.example.flyora_backend.dynamo.models.SystemLogDynamoDB;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Repository
public class SystemLogRepository extends AbstractDynamoRepository<SystemLogDynamoDB> {
    public SystemLogRepository(DynamoDbEnhancedClient client) { super(client, SystemLogDynamoDB.class, "SystemLog"); }

    public List<SystemLogDynamoDB> findAllByOrderByCreatedAtDesc() {
        List<SystemLogDynamoDB> list = findAll();
        // Sort Java
        list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())); 
        return list;
    }
}