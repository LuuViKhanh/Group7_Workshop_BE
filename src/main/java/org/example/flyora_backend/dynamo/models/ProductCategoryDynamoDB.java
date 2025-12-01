package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class ProductCategoryDynamoDB {
    private Integer id;
    private String name;
    private Integer productId; 

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("name") public String getName() { return name; }

    @DynamoDbSecondaryPartitionKey(indexNames = "product_id-index")
    @DynamoDbAttribute("product_id") 
    public Integer getProductId() { return productId; }
}