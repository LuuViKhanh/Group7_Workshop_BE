package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class ProductImageDynamoDB {
    private Integer id;
    private String imageUrl;
    private Integer status;
    private Integer productId;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("image_url") public String getImageUrl() { return imageUrl; }
    @DynamoDbAttribute("status") public Integer getStatus() { return status; }
    @DynamoDbAttribute("product_id") public Integer getProductId() { return productId; }
}
