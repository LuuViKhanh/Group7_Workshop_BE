package org.example.flyora_backend.repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractDynamoRepository<T> {
    protected final DynamoDbTable<T> table;

    public AbstractDynamoRepository(DynamoDbEnhancedClient client, Class<T> type, String tableName) {
        this.table = client.table(tableName, TableSchema.fromBean(type));
    }

    public void save(T item) {
        table.putItem(item);
    }

    public Optional<T> findById(Integer id) {
        return Optional.ofNullable(table.getItem(Key.builder().partitionValue(id).build()));
    }

    public void deleteById(Integer id) {
        table.deleteItem(Key.builder().partitionValue(id).build());
    }

    public List<T> findAll() {
        return table.scan().items().stream().collect(Collectors.toList());
    }
    
    // Hàm thay thế findMaxId (Cảnh báo: Hiệu năng thấp nếu bảng lớn)
    public synchronized Integer generateNewId() {
        return findAll().stream()
                .map(item -> {
                     // Giả sử class T có method getId() trả về Integer
                     // Cần reflection hoặc interface để lấy ID chuẩn hơn, 
                     // nhưng ở đây mình demo đơn giản
                     try {
                         return (Integer) item.getClass().getMethod("getId").invoke(item);
                     } catch (Exception e) { return 0; }
                })
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    /**
     * Trả về giá trị ID lớn nhất hiện có trong bảng (không +1).
     * Sử dụng Optional để caller có thể xử lý khi bảng rỗng.
     * Cảnh báo: phương pháp này quét toàn bộ bảng và có thể chậm với dữ liệu lớn.
     */
    public Optional<Integer> findMaxId() {
        return findAll().stream()
                .map(item -> {
                    try {
                        return (Integer) item.getClass().getMethod("getId").invoke(item);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .max(Integer::compareTo);
    }
}