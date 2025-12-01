package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class BirdTypeDynamoDB {
    private Integer id; private String name;
    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("name") public String getName() { return name; }
}