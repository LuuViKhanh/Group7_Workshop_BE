package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class IssueReportDynamoDB {
    private Integer id;
    private Integer customerId;
    private Integer orderId;
    private String content;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("customer_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "customer_id-index")
    public Integer getCustomerId() { return customerId; }
    
    @DynamoDbAttribute("order_id") public Integer getOrderId() { return orderId; }
    @DynamoDbAttribute("content") public String getContent() { return content; }

}