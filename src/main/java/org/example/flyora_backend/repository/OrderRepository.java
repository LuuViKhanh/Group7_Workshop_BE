package org.example.flyora_backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.flyora_backend.dynamo.models.OrderDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class OrderRepository extends AbstractDynamoRepository<OrderDynamoDB> {

    public OrderRepository(DynamoDbEnhancedClient client) {
        super(client, OrderDynamoDB.class, "Order");
    }

    // 1. Tìm Order theo Customer ID (Dùng GSI: customer_id-index)
    public List<OrderDynamoDB> findByCustomerId(Integer customerId) {
        return table.index("customer_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(customerId)))
                .stream()
                .flatMap(p -> p.items().stream())
                // Sắp xếp giảm dần theo ngày tạo (xử lý ở Java)
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    // 2. Tìm theo Order Code (Dùng Scan vì code này unique nhưng chưa chắc bạn đã tạo GSI)
    // Nếu có GSI cho order_code thì dùng index query sẽ nhanh hơn.
    public Optional<OrderDynamoDB> findByOrderCode(String orderCode) {
        return table.scan().items().stream()
                .filter(o -> orderCode.equals(o.getOrderCode()))
                .findFirst();
    }

    // 3. Xóa theo Customer ID
    public void deleteByCustomerId(Integer customerId) {
        List<OrderDynamoDB> orders = findByCustomerId(customerId);
        for (OrderDynamoDB order : orders) {
            deleteById(order.getId());
        }
    }
}