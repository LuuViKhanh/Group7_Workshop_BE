package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class AccountDynamoDB {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer isActive;
    private Integer roleId;
    private Integer isApproved;
    private Integer approvedBy;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() {
        return id;
    }

    @DynamoDbAttribute("username")
    @DynamoDbSecondaryPartitionKey(indexNames = "username-index")
    public String getUsername() {
        return username;
    }

    @DynamoDbAttribute("password") public String getPassword() { return password; }
    @DynamoDbAttribute("email") public String getEmail() { return email; }
    @DynamoDbAttribute("phone") public String getPhone() { return phone; }
    @DynamoDbAttribute("is_active") public Integer getIsActive() { return isActive; }
    @DynamoDbAttribute("role_id") public Integer getRoleId() { return roleId; }
    @DynamoDbAttribute("is_approved") public Integer getIsApproved() { return isApproved; }
    @DynamoDbAttribute("approved_by") public Integer getApprovedBy() { return approvedBy; }
}