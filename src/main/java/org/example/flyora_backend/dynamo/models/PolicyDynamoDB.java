package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class PolicyDynamoDB {
    private Integer id;
    private String content;
    private Integer updatedBy;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("content") public String getContent() { return content; }

    @DynamoDbAttribute("updated_by") 
    @DynamoDbSecondaryPartitionKey(indexNames = "updated_by-index")
    public Integer getUpdatedBy() { return updatedBy; }
}