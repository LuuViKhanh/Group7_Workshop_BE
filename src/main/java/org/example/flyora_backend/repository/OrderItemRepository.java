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

    /** Thêm hàm scan toàn bảng (hỗ trợ tính doanh số) */
    public List<OrderItemDynamoDB> findAll() {
        return table.scan().items().stream().collect(Collectors.toList());
    }

    /** Hàm tính tổng số lượng đã bán theo từng product */
    public Map<Integer, Integer> findTotalSalesForProducts(List<Integer> productIds) {
        List<OrderItemDynamoDB> allItems = findAll();

        return allItems.stream()
                .filter(item -> productIds.contains(item.getProductId()))
                .collect(Collectors.groupingBy(
                        OrderItemDynamoDB::getProductId,
                        Collectors.summingInt(OrderItemDynamoDB::getQuantity)
                ));
    }

    public void deleteByOrderId(Integer orderId) {
        List<OrderItemDynamoDB> items = table.index("order_id-index")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(orderId)))
                .stream().flatMap(p -> p.items().stream()).collect(Collectors.toList());
        
        for (OrderItemDynamoDB item : items) deleteById(item.getId());
    }
}
