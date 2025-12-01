package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class SystemLogDynamoDB {
    private Integer id;
    private Integer adminId;
    private String action;
    private String createdAt;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("admin_id") public Integer getAdminId() { return adminId; }
    @DynamoDbAttribute("action") public String getAction() { return action; }
    @DynamoDbAttribute("created_at") public String getCreatedAt() { return createdAt; }

}