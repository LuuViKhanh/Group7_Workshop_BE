package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class NotificationDynamoDB {
    private Integer id;
    private Integer recipientId; // FK Account
    private String content;
    private String createdAt;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class) 
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("recipient_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "recipient_id-index")
    public Integer getRecipientId() { return recipientId; }
    
    @DynamoDbAttribute("content") public String getContent() { return content; }
    @DynamoDbAttribute("created_at") public String getCreatedAt() { return createdAt; }

}