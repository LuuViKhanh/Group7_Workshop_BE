package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.NotificationDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NotificationRepository extends AbstractDynamoRepository<NotificationDynamoDB> {

    public NotificationRepository(DynamoDbEnhancedClient client) {
        super(client, NotificationDynamoDB.class, "Notification");
    }

    public List<NotificationDynamoDB> findByRecipientIdOrderByCreatedAtDesc(Integer recipientId) {
        List<NotificationDynamoDB> list = table.index("recipient_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(recipientId)))
                .stream()
                .flatMap(p -> p.items().stream())
                .collect(Collectors.toList());

        list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return list;
    }

    public void deleteByRecipientId(Integer recipientId) {
        List<NotificationDynamoDB> list = findByRecipientIdOrderByCreatedAtDesc(recipientId);
        for (NotificationDynamoDB item : list) {
            deleteById(item.getId());
        }
    }
}