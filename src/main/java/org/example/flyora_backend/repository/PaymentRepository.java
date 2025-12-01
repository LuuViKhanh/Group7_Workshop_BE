package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.PaymentDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PaymentRepository extends AbstractDynamoRepository<PaymentDynamoDB> {

    public PaymentRepository(DynamoDbEnhancedClient client) {
        super(client, PaymentDynamoDB.class, "Payment");
    }

    public void deleteByCustomerId(Integer customerId) {
        List<PaymentDynamoDB> list = table.index("customer_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(customerId)))
                .stream()
                .flatMap(p -> p.items().stream())
                .collect(Collectors.toList());

        for (PaymentDynamoDB item : list) {
            deleteById(item.getId());
        }
    }
}