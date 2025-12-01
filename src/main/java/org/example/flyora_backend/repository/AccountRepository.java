package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.*;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;

@Repository
public class AccountRepository extends AbstractDynamoRepository<AccountDynamoDB> {

    public AccountRepository(DynamoDbEnhancedClient client) {
        super(client, AccountDynamoDB.class, "Account"); // Tên bảng khớp DynamoDB
    }

    public Optional<AccountDynamoDB> findByUsername(String username) {
        // YÊU CẦU: Tạo GSI tên "username-index" trong DynamoDB
        return table.index("username-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(username)))
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    public Integer existsByUsername(String username) {
        return findByUsername(username).isPresent() ? 1 : 0;
    }
}