package org.example.flyora_backend.dynamo.models;

import java.math.BigDecimal;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class OrderItemDynamoDB {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private BigDecimal price;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("order_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "order_id-index")
    public Integer getOrderId() { return orderId; }
    
    @DynamoDbAttribute("product_id") public Integer getProductId() { return productId; }
    @DynamoDbAttribute("quantity") public Integer getQuantity() { return quantity; }
    @DynamoDbAttribute("price") public BigDecimal getPrice() { return price; }

}