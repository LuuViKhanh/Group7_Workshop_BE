package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class FaqDynamoDB {
    private Integer id;
    private String question;
    private String answer;
    private Integer updatedBy;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("question") public String getQuestion() { return question; }
    @DynamoDbAttribute("answer") public String getAnswer() { return answer; }

    @DynamoDbAttribute("updated_by") 
    @DynamoDbSecondaryPartitionKey(indexNames = "updated_by-index")
    public Integer getUpdatedBy() { return updatedBy; }

}