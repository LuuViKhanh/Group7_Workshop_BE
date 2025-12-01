package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.IssueReportDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class IssueReportRepository extends AbstractDynamoRepository<IssueReportDynamoDB> {

    public IssueReportRepository(DynamoDbEnhancedClient client) {
        super(client, IssueReportDynamoDB.class, "IssueReport");
    }

    public void deleteByCustomerId(Integer customerId) {
        List<IssueReportDynamoDB> list = table.index("customer_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(customerId)))
                .stream()
                .flatMap(p -> p.items().stream())
                .collect(Collectors.toList());

        for (IssueReportDynamoDB item : list) {
            deleteById(item.getId());
        }
    }
}