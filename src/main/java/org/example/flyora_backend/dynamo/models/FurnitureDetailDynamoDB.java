package org.example.flyora_backend.dynamo.models;

import java.math.BigDecimal;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class FurnitureDetailDynamoDB {
    private Integer id;
    private Integer productId;
    private String material;
    private BigDecimal weight;
    private String color;
    private String origin;
    private String dimensions;
    private String imageUrl;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("product_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "product_id-index")
    public Integer getProductId() { return productId; }
    
    @DynamoDbAttribute("material") public String getMaterial() { return material; }
    @DynamoDbAttribute("weight") public BigDecimal getWeight() { return weight; }
    @DynamoDbAttribute("color") public String getColor() { return color; }
    @DynamoDbAttribute("origin") public String getOrigin() { return origin; }
    @DynamoDbAttribute("dimensions") public String getDimensions() { return dimensions; }
    @DynamoDbAttribute("image_url") public String getImageUrl() { return imageUrl; }
}