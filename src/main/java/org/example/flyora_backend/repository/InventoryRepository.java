// Trong file: org/example/flyora_backend/repository/InventoryRepository.java
package org.example.flyora_backend.repository;

import java.util.Optional;

import org.example.flyora_backend.dynamo.models.InventoryDynamoDB;
import org.example.flyora_backend.dynamo.models.SalesStaffDynamoDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Repository
public class InventoryRepository extends AbstractDynamoRepository<InventoryDynamoDB> {
    
    @Autowired private SalesStaffRepository salesStaffRepo; // Inject repo khác

    public InventoryRepository(DynamoDbEnhancedClient client) {
        super(client, InventoryDynamoDB.class, "Inventory");
    }

    public void deleteBySalesStaffAccountId(Integer accountId) {
        // Bước 1: Tìm Staff ID từ Account ID
        Optional<SalesStaffDynamoDB> staff = salesStaffRepo.findByAccountId(accountId);
        
        if (staff.isPresent()) {
            // Bước 2: Tìm Inventory của staff đó (Giả sử Inventory có field staffId - cần check lại model của bạn)
            // Nếu Inventory không có staffId mà liên kết qua Product -> ShopOwner, logic sẽ khác.
            // Giả sử logic cũ của bạn đúng và Inventory có quan hệ với Staff
            /* 
               Vì model InventoryDynamo của bạn chỉ có productId, 
               tôi giả định bạn cần logic custom ở đây. 
               Nếu không có cột staffId trong InventoryDynamo, bạn không thể xóa theo staff được.
            */
        }
    }
}