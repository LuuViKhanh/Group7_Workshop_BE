package org.example.flyora_backend.repository;

import java.util.Optional;

import org.example.flyora_backend.dynamo.models.SalesStaffDynamoDB;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class SalesStaffRepository extends AbstractDynamoRepository<SalesStaffDynamoDB> {
    public SalesStaffRepository(DynamoDbEnhancedClient client) { super(client, SalesStaffDynamoDB.class, "SalesStaff"); }

    public Optional<SalesStaffDynamoDB> findByAccountId(Integer accountId) {
        return table.index("account_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(accountId)))
                .stream().flatMap(p -> p.items().stream()).findFirst();
    }
}
