package org.example.flyora_backend.DTOs;

import org.example.flyora_backend.dynamo.models.AccountDynamoDB;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private Integer isActive;
    private Integer isApproved;
    private String role;

    /**
     * Constructor mới cho DynamoDB.
     * Vì DynamoDB không join bảng, bạn cần truyền roleName từ bên ngoài vào.
     * 
     * @param account Entity lấy từ DynamoDB
     * @param roleName Tên role đã được query riêng từ bảng Role (hoặc cache)
     */
    public UserDTO(AccountDynamoDB account, String roleName) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.phone = account.getPhone();
        
        // Xử lý null safety cho Integer vì DynamoDB có thể trả về null
        this.isActive = account.getIsActive() != null ? account.getIsActive() : 1;
        this.isApproved = account.getIsApproved() != null ? account.getIsApproved() : 1;
        
        this.role = roleName;
    }
    
    public UserDTO(AccountDynamoDB account) {
        this(account, null);
    }
}