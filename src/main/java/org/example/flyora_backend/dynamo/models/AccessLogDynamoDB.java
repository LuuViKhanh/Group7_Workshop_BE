package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter // Chỉ dùng Lombok cho Setter để code gọn
public class AccessLogDynamoDB {

    private Integer id;

    private Integer accountId; // Trong DB có thể là 'account_id'
    private String action;
    private String timestamp;  // MySQL export ra text, nên để String là an toàn nhất

    // 1. KHÓA CHÍNH (Primary Key)
    // Lưu ý: Kiểm tra xem trong DynamoDB cột này tên là "id" hay "ID"?
    @DynamoDbPartitionKey
    @DynamoDbAttribute("id") 
    @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() {
        return id;
    }

    // 2. MAPPING CỘT account_id
    // Quan trọng: Cần map đúng tên cột trong file CSV MySQL cũ
    @DynamoDbAttribute("account_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "account_id-index")
    public Integer getAccountId() {
        return accountId;
    }

    // 3. Các trường bình thường
    // Nếu tên trong DB là "action" (giống Java) thì không cần annotation, 
    // nhưng nên thêm @DynamoDbAttribute để tường minh nếu muốn chắc chắn.
    @DynamoDbAttribute("action")
    public String getAction() {
        return action;
    }

    @DynamoDbAttribute("timestamp")
    public String getTimestamp() {
        return timestamp;
    }
}