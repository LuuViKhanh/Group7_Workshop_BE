package org.example.flyora_backend.dynamo.models;

import java.math.BigDecimal;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class ProductDynamoDB {
    private Integer id;
    private String description;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private Integer categoryId;  // FK
    private Integer birdTypeId;  // FK
    private Integer shopOwnerId; // FK
    private Integer salesCount;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("description") public String getDescription() { return description; }
    @DynamoDbAttribute("name") public String getName() { return name; }
    @DynamoDbAttribute("price") public BigDecimal getPrice() { return price; }
    @DynamoDbAttribute("stock") public Integer getStock() { return stock; }
    @DynamoDbAttribute("status") public Integer getStatus() { return status; }
    @DynamoDbAttribute("category_id") public Integer getCategoryId() { return categoryId; }
    @DynamoDbAttribute("bird_type_id") public Integer getBirdTypeId() { return birdTypeId; }

    @DynamoDbAttribute("shop_owner_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "shop_owner_id-index")
    public Integer getShopOwnerId() { return shopOwnerId; }
    
    @DynamoDbAttribute("sales_count") public Integer getSalesCount() { return salesCount; }
}
