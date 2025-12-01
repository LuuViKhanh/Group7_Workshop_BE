package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class ProductReviewDynamoDB {
    private Integer id;
    private Integer customerId;
    private Integer productId;
    private String review;
    private Integer rating;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    // GSI: customer_id-index
    @DynamoDbSecondaryPartitionKey(indexNames = {"customer_id-index"})
    @DynamoDbAttribute("customer_id")
    public Integer getCustomerId() { return customerId; }

    // GSI: product_id-index
    @DynamoDbSecondaryPartitionKey(indexNames = {"product_id-index"})
    @DynamoDbAttribute("product_id")
    public Integer getProductId() { return productId; }

    @DynamoDbAttribute("review")
    public String getReview() { return review; }

    @DynamoDbAttribute("rating")
    public Integer getRating() { return rating; }
}
