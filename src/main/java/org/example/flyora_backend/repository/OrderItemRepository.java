// src/main/java/org/example/flyora_backend/repository/OrderItemRepository.java
package org.example.flyora_backend.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.example.flyora_backend.dynamo.models.OrderItemDynamoDB;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class OrderItemRepository extends AbstractDynamoRepository<OrderItemDynamoDB> {

    public OrderItemRepository(DynamoDbEnhancedClient client) {
        super(client, OrderItemDynamoDB.class, "OrderItem");
    }

    // Hàm thay thế logic SQL: SUM(quantity) GROUP BY product_id
    public Map<Integer, Integer> findTotalSalesForProducts(List<Integer> productIds) {
        // 1. Scan lấy toàn bộ OrderItem (Hoặc query theo danh sách order nếu có logic khác)
        // Lưu ý: Nếu bảng lớn, nên cache kết quả này hoặc dùng DynamoDB Streams để update count sang bảng Product
        List<OrderItemDynamoDB> allItems = findAll();

        // 2. Filter và Group bằng Java Stream
        return allItems.stream()
                .filter(item -> productIds.contains(item.getProductId()))
                .collect(Collectors.groupingBy(
                        OrderItemDynamoDB::getProductId,
                        Collectors.summingInt(OrderItemDynamoDB::getQuantity)
                ));
    }

    public void deleteByOrderId(Integer orderId) {
        // Cần GSI: order_id-index
        List<OrderItemDynamoDB> items = table.index("order_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(orderId)))
                .stream().flatMap(p -> p.items().stream()).collect(Collectors.toList());
        
        for (OrderItemDynamoDB item : items) deleteById(item.getId());
    }
}