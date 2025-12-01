package org.example.flyora_backend.dynamo.models;

import java.math.BigDecimal;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class FoodDetailDynamoDB {
    private Integer id;
    private Integer productId;
    private String material;
    private String origin;
    private String usageTarget;
    private BigDecimal weight;
    private String imageUrl;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("product_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "product_id-index")
    public Integer getProductId() { return productId; }
    
    @DynamoDbAttribute("material") public String getMaterial() { return material; }
    @DynamoDbAttribute("origin") public String getOrigin() { return origin; }
    @DynamoDbAttribute("usage_target") public String getUsageTarget() { return usageTarget; }
    @DynamoDbAttribute("weight") public BigDecimal getWeight() { return weight; }
    @DynamoDbAttribute("image_url") public String getImageUrl() { return imageUrl; }
}