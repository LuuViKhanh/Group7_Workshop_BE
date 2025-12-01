package org.example.flyora_backend.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.example.flyora_backend.dynamo.models.FaqDynamoDB;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class FaqRepository extends AbstractDynamoRepository<FaqDynamoDB> {
    public FaqRepository(DynamoDbEnhancedClient client) { super(client, FaqDynamoDB.class, "Faq"); }

    public void clearUpdatedBy(Integer accountId) {
        // Cần GSI: updated_by-index để tìm nhanh các bài viết do người này update
        List<FaqDynamoDB> list = table.index("updated_by-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(accountId)))
                .stream().flatMap(p -> p.items().stream()).collect(Collectors.toList());

        for (FaqDynamoDB faq : list) {
            faq.setUpdatedBy(null);
            save(faq); // Update lại vào DB
        }
    }
}