package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class ShopOwnerDynamoDB {
    private Integer id;
    private String name;
    private String otherInfo;
    private Integer accountId;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("name") public String getName() { return name; }
    @DynamoDbAttribute("other_info") public String getOtherInfo() { return otherInfo; }

    @DynamoDbAttribute("account_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "account_id-index")
    public Integer getAccountId() { return accountId; }
}