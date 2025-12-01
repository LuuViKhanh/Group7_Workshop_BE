package org.example.flyora_backend.dynamo.models;

import java.math.BigDecimal;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class OrderDynamoDB {
    private Integer id;
    private String orderCode;
    private String createdAt; // Timestamp -> String
    private String status;
    private BigDecimal totalAmount;
    private Integer customerId;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("code") public String getOrderCode() { return orderCode; }
    @DynamoDbAttribute("created_at") public String getCreatedAt() { return createdAt; }
    @DynamoDbAttribute("status") public String getStatus() { return status; }
    @DynamoDbAttribute("total_amount") public BigDecimal getTotalAmount() { return totalAmount; }

    @DynamoDbAttribute("customer_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "customer_id-index")
    public Integer getCustomerId() { return customerId; }

}