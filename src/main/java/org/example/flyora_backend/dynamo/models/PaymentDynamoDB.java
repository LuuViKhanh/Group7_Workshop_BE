package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class PaymentDynamoDB {
    private Integer id;
    private Integer orderId;
    private Integer customerId;
    private String status;
    private String paidAt; // Instant -> String

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("order_id") public Integer getOrderId() { return orderId; }

    @DynamoDbAttribute("customer_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "customer_id-index")
    public Integer getCustomerId() { return customerId; }

    @DynamoDbAttribute("status") public String getStatus() { return status; }
    @DynamoDbAttribute("paid_at") public String getPaidAt() { return paidAt; }
}