package org.example.flyora_backend.repository;

import org.example.flyora_backend.dynamo.models.RoleDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.Optional;

@Repository
public class RoleRepository extends AbstractDynamoRepository<RoleDynamoDB> {
    public RoleRepository(DynamoDbEnhancedClient client) {
        super(client, RoleDynamoDB.class, "Role");
    }

    // Cần GSI "name-index" nếu muốn tìm theo tên, hoặc dùng Scan (vì bảng Role thường ít item)
    public Optional<RoleDynamoDB> findByName(String name) {
        return findAll().stream()
                .filter(r -> r.getName().equals(name))
                .findFirst();
    }
}