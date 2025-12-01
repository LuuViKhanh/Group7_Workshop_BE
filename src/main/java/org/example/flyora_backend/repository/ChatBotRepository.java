package org.example.flyora_backend.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.example.flyora_backend.dynamo.models.ChatBotDynamoDB;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class ChatBotRepository extends AbstractDynamoRepository<ChatBotDynamoDB> {
    public ChatBotRepository(DynamoDbEnhancedClient client) { super(client, ChatBotDynamoDB.class, "ChatBot"); }

    public List<ChatBotDynamoDB> findByCustomerId(Integer customerId) {
        // Cần GSI: customer_id-index
        List<ChatBotDynamoDB> list = table.index("customer_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(customerId)))
                .stream().flatMap(p -> p.items().stream()).collect(Collectors.toList());
        
        list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return list;
    }
}
