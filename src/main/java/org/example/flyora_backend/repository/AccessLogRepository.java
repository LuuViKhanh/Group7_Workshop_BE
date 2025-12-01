package org.example.flyora_backend.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.example.flyora_backend.dynamo.models.AccessLogDynamoDB;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class AccessLogRepository extends AbstractDynamoRepository<AccessLogDynamoDB> {
    public AccessLogRepository(DynamoDbEnhancedClient client) {
        super(client, AccessLogDynamoDB.class, "AccessLog");
    }

    public void deleteByAccountId(Integer accountId) {
        List<AccessLogDynamoDB> logs = table.index("account_id-index").query(QueryConditional.keyEqualTo(k -> k.partitionValue(accountId))).stream().flatMap(page -> page.items().stream()).collect(Collectors.toList());
        
        for (AccessLogDynamoDB log : logs) {
            deleteById(log.getId());
        }
    }
}
