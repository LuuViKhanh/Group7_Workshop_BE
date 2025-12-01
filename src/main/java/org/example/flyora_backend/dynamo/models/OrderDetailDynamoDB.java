package org.example.flyora_backend.dynamo.models;

import java.math.BigDecimal;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Getter
@Setter
public class OrderDetailDynamoDB {
    private Integer id;
    private Integer quantity;
    private BigDecimal price;
    private Integer status = 1;
    private Integer order_id;
    private Integer product_id;

    @DynamoDbPartitionKey
    @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() {
        return id;
    }
}

