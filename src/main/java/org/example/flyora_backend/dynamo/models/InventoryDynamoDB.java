package org.example.flyora_backend.dynamo.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;

@Setter
@DynamoDbBean
public class InventoryDynamoDB {
    private Integer id;
    private Integer productId;
    private Integer quantity;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("product_id") public Integer getProductId() { return productId; }
    @DynamoDbAttribute("quantity") public Integer getQuantity() { return quantity; }

}