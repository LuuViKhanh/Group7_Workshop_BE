package org.example.flyora_backend.repository;

import java.util.Optional;

import org.example.flyora_backend.dynamo.models.CustomerDynamoDB;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class CustomerRepository extends AbstractDynamoRepository<CustomerDynamoDB> {
    public CustomerRepository(DynamoDbEnhancedClient client) {
        super(client, CustomerDynamoDB.class, "Customer");
    }

    public Optional<CustomerDynamoDB> findByAccountId(Integer accountId) {
        return table.index("account_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(accountId)))
                .stream().flatMap(p -> p.items().stream()).findFirst();
    }
    
    public void deleteByAccountId(Integer accountId) {
        findByAccountId(accountId).ifPresent(c -> deleteById(c.getId()));
    }
}
