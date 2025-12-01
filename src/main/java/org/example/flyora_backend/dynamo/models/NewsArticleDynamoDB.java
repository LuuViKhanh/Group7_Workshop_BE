package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class NewsArticleDynamoDB {
    private Integer id;
    private String title;
    private String url;
    private String createdAt;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("title") public String getTitle() { return title; }
    @DynamoDbAttribute("url") public String getUrl() { return url; }
    @DynamoDbAttribute("created_at") public String getCreatedAt() { return createdAt; }

}