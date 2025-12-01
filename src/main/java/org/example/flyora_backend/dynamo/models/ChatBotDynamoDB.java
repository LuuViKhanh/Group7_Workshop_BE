package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class ChatBotDynamoDB {
    private Integer id;
    private Integer customerId;
    private String message;
    private String response;
    private String createdAt;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("customer_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "customer_id-index")
    public Integer getCustomerId() { return customerId; }
    
    @DynamoDbAttribute("message") public String getMessage() { return message; }
    @DynamoDbAttribute("response") public String getResponse() { return response; }
    @DynamoDbAttribute("created_at") public String getCreatedAt() { return createdAt; }

} 