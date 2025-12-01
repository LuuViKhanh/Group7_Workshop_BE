package org.example.flyora_backend.Utils;

import javax.annotation.PostConstruct;
import org.example.flyora_backend.dynamo.models.*; // Import các model cần thiết
import org.example.flyora_backend.repository.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IdGeneratorUtil {

    // Sử dụng một Map để lưu trữ bộ đếm ID cho tất cả các thực thể
    // AtomicInteger để đảm bảo an toàn trong môi trường đa luồng
    private final Map<String, AtomicInteger> idCounters = new ConcurrentHashMap<>();

    // Sử dụng constructor injection, đây là cách làm được khuyến khích
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ProductReviewRepository productReviewRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final FoodDetailRepository foodDetailRepository;
    private final ToyDetailRepository toyDetailRepository;
    private final FurnitureDetailRepository furnitureDetailRepository;
    private final AdminRepository adminRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final SalesStaffRepository salesStaffRepository;
    private final DeliveryNoteRepository deliveryNoteRepository;
    private final SystemLogRepository systemLogRepository;

    public IdGeneratorUtil(AccountRepository accountRepository, CustomerRepository customerRepository,
            ProductReviewRepository productReviewRepository, OrderRepository orderRepository,
            OrderItemRepository orderItemRepository, PaymentRepository paymentRepository,
            ProductRepository productRepository, FoodDetailRepository foodDetailRepository,
            ToyDetailRepository toyDetailRepository, FurnitureDetailRepository furnitureDetailRepository,
            AdminRepository adminRepository, ShopOwnerRepository shopOwnerRepository,
            SalesStaffRepository salesStaffRepository, DeliveryNoteRepository deliveryNoteRepository,
            SystemLogRepository systemLogRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.productReviewRepository = productReviewRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.foodDetailRepository = foodDetailRepository;
        this.toyDetailRepository = toyDetailRepository;
        this.furnitureDetailRepository = furnitureDetailRepository;
        this.adminRepository = adminRepository;
        this.shopOwnerRepository = shopOwnerRepository;
        this.salesStaffRepository = salesStaffRepository;
        this.deliveryNoteRepository = deliveryNoteRepository;
        this.systemLogRepository = systemLogRepository;
    }

    /**
     * Phương thức này sẽ tự động được gọi một lần sau khi IdGeneratorUtil được tạo.
     * Nó sẽ khởi tạo tất cả các bộ đếm ID bằng cách lấy giá trị lớn nhất từ DB.
     */
    @PostConstruct
    public void init() {
        idCounters.put(AccountDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(accountRepository.findMaxId().orElse(null))));
        idCounters.put(CustomerDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(customerRepository.findMaxId().orElse(null))));
        idCounters.put(ProductReviewDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(productReviewRepository.findMaxId().orElse(null))));
        idCounters.put(OrderDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(orderRepository.findMaxId().orElse(null))));
        idCounters.put(OrderItemDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(orderItemRepository.findMaxId().orElse(null))));
        idCounters.put(PaymentDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(paymentRepository.findMaxId().orElse(null))));
        idCounters.put(ProductDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(productRepository.findMaxId().orElse(null))));
        idCounters.put(FoodDetailDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(foodDetailRepository.findMaxId().orElse(null))));
        idCounters.put(ToyDetailDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(toyDetailRepository.findMaxId().orElse(null))));
        idCounters.put(FurnitureDetailDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(furnitureDetailRepository.findMaxId().orElse(null))));
        idCounters.put(AdminDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(adminRepository.findMaxId().orElse(null))));
        idCounters.put(ShopOwnerDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(shopOwnerRepository.findMaxId().orElse(null))));
        idCounters.put(SalesStaffDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(salesStaffRepository.findMaxId().orElse(null))));
        idCounters.put(DeliveryNoteDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(deliveryNoteRepository.findMaxId().orElse(null))));
        idCounters.put(SystemLogDynamoDB.class.getSimpleName(),
            new AtomicInteger(safeParseInteger(systemLogRepository.findMaxId().orElse(null))));
    }
    /**
     * Chuyển String sang int an toàn, nếu null hoặc "NULL" trả về 0
     */
    private int safeParseInteger(Object value) {
        if (value == null) return 0;

        String str = value.toString().trim();

        if (str.equalsIgnoreCase("null") || str.isEmpty()) return 0;

        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }




    // Phương thức chung để lấy ID mới
    private Integer getNextId(String entityName) {
        return idCounters.get(entityName).incrementAndGet();
    }

    // Các hàm generate cụ thể giờ chỉ cần gọi hàm chung
    public Integer generateProductReviewId() {
        return getNextId(ProductReviewDynamoDB.class.getSimpleName());
    }

    public Integer generateAccountId() {
        return getNextId(AccountDynamoDB.class.getSimpleName());
    }

    public Integer generateCustomerId() {
        return getNextId(CustomerDynamoDB.class.getSimpleName());
    }

    public Integer generateOrderId() {
        return getNextId(OrderDynamoDB.class.getSimpleName());
    }

    public Integer generateOrderItemId() {
        return getNextId(OrderItemDynamoDB.class.getSimpleName());
    }

    public Integer generatePaymentId() {
        return getNextId(PaymentDynamoDB.class.getSimpleName());
    }

    public Integer generateToyDetailId() {
        return getNextId(ToyDetailDynamoDB.class.getSimpleName());
    }

    public Integer generateFoodDetailId() {
        return getNextId(FoodDetailDynamoDB.class.getSimpleName());
    }

    public Integer generateFurnitureDetailId() {
        return getNextId(FurnitureDetailDynamoDB.class.getSimpleName());
    }

    public Integer generateProductId() {
        return getNextId(ProductDynamoDB.class.getSimpleName());
    }

    public Integer generateAdminId() {
        return getNextId(AdminDynamoDB.class.getSimpleName());
    }

    public Integer generateShopOwnerId() {
        return getNextId(ShopOwnerDynamoDB.class.getSimpleName());
    }

    public Integer generateSalesStaffId() {
        return getNextId(SalesStaffDynamoDB.class.getSimpleName());
    }

    public Integer generateDeliveryNoteId() {
        return getNextId(DeliveryNoteDynamoDB.class.getSimpleName());
    }

    public Integer generateSystemLogId() {
        return getNextId(SystemLogDynamoDB.class.getSimpleName());
    }
}