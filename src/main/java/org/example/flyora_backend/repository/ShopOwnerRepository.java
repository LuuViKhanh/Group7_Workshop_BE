package org.example.flyora_backend.repository;

import java.util.Optional;
import org.example.flyora_backend.dynamo.models.ShopOwnerDynamoDB;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class ShopOwnerRepository extends AbstractDynamoRepository<ShopOwnerDynamoDB> {
    public ShopOwnerRepository(DynamoDbEnhancedClient client) { super(client, ShopOwnerDynamoDB.class, "ShopOwner"); }

    public Optional<ShopOwnerDynamoDB> findByAccountId(Integer accountId) {
        return table.index("account_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(accountId)))
                .stream().flatMap(p -> p.items().stream()).findFirst();
    }
}
