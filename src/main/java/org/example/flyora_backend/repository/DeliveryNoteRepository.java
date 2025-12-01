package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.DeliveryNoteDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DeliveryNoteRepository extends AbstractDynamoRepository<DeliveryNoteDynamoDB> {

    public DeliveryNoteRepository(DynamoDbEnhancedClient client) {
        super(client, DeliveryNoteDynamoDB.class, "DeliveryNote");
    }

    public Optional<DeliveryNoteDynamoDB> findByTrackingNumber(String trackingNumber) {
        // Cần GSI: tracking_number-index
        return table.index("tracking_number-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(trackingNumber)))
                .stream()
                .flatMap(p -> p.items().stream())
                .findFirst();
    }

    public void deleteByOrderId(Integer orderId) {
        // Cần GSI: order_id-index
        List<DeliveryNoteDynamoDB> list = table.index("order_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(orderId)))
                .stream()
                .flatMap(p -> p.items().stream())
                .collect(Collectors.toList());

        for (DeliveryNoteDynamoDB item : list) {
            deleteById(item.getId());
        }
    }
}