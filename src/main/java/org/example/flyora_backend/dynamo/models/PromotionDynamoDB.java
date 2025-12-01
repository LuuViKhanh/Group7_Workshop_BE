package org.example.flyora_backend.dynamo.models;

import java.math.BigDecimal;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class PromotionDynamoDB {
    private Integer id;
    private String code;
    private BigDecimal discount;
    private Integer productId;
    private Integer customerId;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("code") public String getCode() { return code; }
    @DynamoDbAttribute("discount") public BigDecimal getDiscount() { return discount; }
    
    @DynamoDbSecondaryPartitionKey(indexNames = "product_id-index")
    @DynamoDbAttribute("product_id") public Integer getProductId() { return productId; }

    @DynamoDbSecondaryPartitionKey(indexNames = "customer_id-index")
    @DynamoDbAttribute("customer_id") public Integer getCustomerId() { return customerId; }
}