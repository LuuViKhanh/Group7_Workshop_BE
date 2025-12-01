package org.example.flyora_backend.repository;

import java.util.Optional;

import org.example.flyora_backend.dynamo.models.AdminDynamoDB;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class AdminRepository extends AbstractDynamoRepository<AdminDynamoDB> {
    public AdminRepository(DynamoDbEnhancedClient client) { super(client, AdminDynamoDB.class, "Admin"); }

    public Optional<AdminDynamoDB> findByAccountId(Integer accountId) {
        return table.index("account_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(accountId)))
                .stream().flatMap(p -> p.items().stream()).findFirst();
    }
}

