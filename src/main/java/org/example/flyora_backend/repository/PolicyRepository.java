package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.PolicyDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PolicyRepository extends AbstractDynamoRepository<PolicyDynamoDB> {

    public PolicyRepository(DynamoDbEnhancedClient client) {
        super(client, PolicyDynamoDB.class, "Policy");
    }

    public void clearUpdatedBy(Integer accountId) {
        List<PolicyDynamoDB> list = table.index("updated_by-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(accountId)))
                .stream()
                .flatMap(p -> p.items().stream())
                .collect(Collectors.toList());

        for (PolicyDynamoDB policy : list) {
            policy.setUpdatedBy(null);
            save(policy); // Update
        }
    }
}