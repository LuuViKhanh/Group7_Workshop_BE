package org.example.flyora_backend.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.flyora_backend.DTOs.*;
import org.example.flyora_backend.dynamo.models.*;
import org.example.flyora_backend.repository.*;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final DeliveryNoteRepository deliveryNoteRepository;
    private final OrderItemRepository orderItemRepository;
    private final GHNService ghnService;

    // Inject thêm các Detail Repo để lấy cân nặng cho GHN
    private final FoodDetailRepository foodRepo;
    private final ToyDetailRepository toyRepo;
    private final FurnitureDetailRepository furnitureRepo;
    private final ProductCategoryRepository categoryRepo;

    @Override
    public OrderDynamoDB getOrderByCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode).orElse(null);
    }

    @Override
    public void save(OrderDynamoDB order) { // Sửa tham số thành Model mới
        orderRepository.save(order);
    }

    @Override
    public Map<String, Object> createOrder(CreateOrderDTO dto) {
        // 1. Tạo Order
        OrderDynamoDB order = new OrderDynamoDB();
        order.setId(orderRepository.generateNewId());
        order.setCustomerId(dto.getCustomerId());
        order.setStatus("PENDING");
        order.setCreatedAt(Instant.now().toString());
        order.setOrderCode(String.valueOf(System.currentTimeMillis()));

        if (customerRepository.findById(dto.getCustomerId()).isEmpty()) {
            throw new RuntimeException("Customer not found");
        }

        orderRepository.save(order);

        // 2. Xử lý OrderItems
        for (var itemDTO : dto.getItems()) {
            ProductDynamoDB product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStock() < itemDTO.getQuantity()) {
                throw new RuntimeException("Hết hàng: " + product.getName());
            }

            OrderItemDynamoDB orderItem = new OrderItemDynamoDB();
            orderItem.setId(orderItemRepository.generateNewId());
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setOrderId(order.getId());

            orderItemRepository.save(orderItem);
        }

        return Map.of("orderId", order.getId(), "orderCode", order.getOrderCode(), "status", order.getStatus());
    }

    @Override
    public Map<String, Object> createPayment(CreatePaymentDTO dto) {
        // 1. Lấy Order
        OrderDynamoDB order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomerId().equals(dto.getCustomerId())) {
            throw new RuntimeException("Customer ID không khớp.");
        }

        // 2. Xử lý GHN (Nếu phương thức thanh toán là COD - ID 2)
        if (dto.getPaymentMethodId() == 2) {
            try {
                // Logic GHN phức tạp đã được tách ra hàm riêng bên dưới
                CreateOrderRequestDTO ghnRequest = buildGhnRequest(order, dto);
                Map<String, Object> ghnResponse = ghnService.createOrder(ghnRequest);

                String trackingNumber = (String) ghnResponse.get("order_code");
                
                // Lưu Delivery Note
                DeliveryNoteDynamoDB deliveryNote = new DeliveryNoteDynamoDB();
                deliveryNote.setId(deliveryNoteRepository.generateNewId());
                deliveryNote.setOrderId(order.getId());
                deliveryNote.setTrackingNumber(trackingNumber);
                deliveryNote.setShippingMethodId(1); // Hardcode method ID 1
                deliveryNote.setDeliveryPartnerName("GHN");
                deliveryNote.setStatus("ready_to_pick");
                deliveryNoteRepository.save(deliveryNote);

                // Cập nhật trạng thái đơn hàng
                order.setStatus("Shipping");
                
                // Trừ kho (Cần tìm lại items để trừ)
                List<OrderItemDynamoDB> items = getItemsByOrderId(order.getId());
                for (OrderItemDynamoDB item : items) {
                    ProductDynamoDB product = productRepository.findById(item.getProductId()).orElse(null);
                    if (product != null) {
                        product.setStock(product.getStock() - item.getQuantity());
                        productRepository.save(product);
                    }
                }
                orderRepository.save(order);

            } catch (Exception e) {
                throw new RuntimeException("Tạo đơn vận chuyển thất bại: " + e.getMessage(), e);
            }
        }

        // 3. Lưu Payment
        PaymentDynamoDB payment = new PaymentDynamoDB();
        payment.setId(paymentRepository.generateNewId());
        payment.setOrderId(order.getId());
        payment.setCustomerId(order.getCustomerId());
        payment.setStatus("PENDING_COD");
        paymentRepository.save(payment);

        return Map.of("paymentId", payment.getId(), "orderStatus", order.getStatus());
    }

    @Override
    public void attachOrderCode(Integer orderId, String orderCode) {
        OrderDynamoDB order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderCode(orderCode);
        orderRepository.save(order);
    }

    @Override
    public List<OrderHistoryDTO> getOrdersByCustomer(Integer customerId) {
        List<OrderDynamoDB> orders = orderRepository.findByCustomerId(customerId);

        return orders.stream().map(order -> {
            // Manual Join: Lấy items của order này
            List<OrderItemDynamoDB> items = getItemsByOrderId(order.getId());

            List<OrderDetailDTO> details = items.stream().map(item -> {
                String productName = productRepository.findById(item.getProductId())
                        .map(ProductDynamoDB::getName).orElse("Unknown Product");
                return new OrderDetailDTO(
                        item.getProductId(),
                        productName,
                        item.getQuantity(),
                        item.getPrice());
            }).collect(Collectors.toList());

            return new OrderHistoryDTO(
                    order.getId(),
                    Timestamp.from(Instant.parse(order.getCreatedAt())), // Convert String -> Timestamp
                    order.getStatus(),
                    order.getOrderCode(),
                    details);
        }).collect(Collectors.toList());
    }

    // =================================================================
    // HELPER METHODS (Thay thế cho việc gọi order.getOrderDetails())
    // =================================================================

    // 1. Hàm lấy Items theo OrderID (Vì chưa có GSI trên OrderItem nên phải scan hoặc tạo GSI)
    private List<OrderItemDynamoDB> getItemsByOrderId(Integer orderId) {
        // *Lưu ý*: Bạn nên tạo GSI 'order_id-index' trong OrderItemRepository như tôi đã nhắc ở câu trước
        // Ở đây tôi giả sử bạn đã có method findByOrderId trong OrderItemRepository
        // Nếu chưa, bạn phải dùng scan:
        return orderItemRepository.findAll().stream()
                .filter(i -> i.getOrderId().equals(orderId))
                .collect(Collectors.toList());
    }

    // 2. Hàm Build GHN Request (Thay thế createGhnRequestFromOrder cũ)
    private CreateOrderRequestDTO buildGhnRequest(OrderDynamoDB order, CreatePaymentDTO paymentDTO) {
        CreateOrderRequestDTO ghnRequest = new CreateOrderRequestDTO();
        
        // Lấy thông tin Customer
        CustomerDynamoDB customer = customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ghnRequest.setTo_name(customer.getName());
        ghnRequest.setTo_phone("0942921287"); // Nên lấy từ Customer nếu có
        ghnRequest.setTo_address("123 Đường ABC");
        ghnRequest.setTo_ward_code("20309");
        ghnRequest.setTo_district_id(1459);

        // Lấy danh sách items
        List<OrderItemDynamoDB> items = getItemsByOrderId(order.getId());
        
        // Tính tổng tiền COD
        BigDecimal codAmount = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ghnRequest.setCod_amount(codAmount.intValue());
        ghnRequest.setContent("Flyora - Don hang #" + order.getId());

        // Tính tổng cân nặng (Phức tạp vì phải query bảng Detail)
        int totalWeight = 0;
        List<ItemDTO> ghnItems = new ArrayList<>();

        for (OrderItemDynamoDB item : items) {
            ProductDynamoDB product = productRepository.findById(item.getProductId()).orElse(null);
            if (product == null) continue;

            // Thêm vào list item của GHN
            ItemDTO ghnItem = new ItemDTO();
            ghnItem.setName(product.getName());
            ghnItem.setQuantity(item.getQuantity());
            ghnItem.setPrice(item.getPrice().intValue());
            ghnItems.add(ghnItem);

            // Lấy cân nặng từ bảng Detail tương ứng
            BigDecimal weight = BigDecimal.ZERO;
            String catName = categoryRepo.findById(product.getCategoryId())
                    .map(ProductCategoryDynamoDB::getName).orElse("");

            if ("FOODS".equalsIgnoreCase(catName)) {
                weight = foodRepo.findByProductId(product.getId()).map(FoodDetailDynamoDB::getWeight).orElse(BigDecimal.ZERO);
            } else if ("TOYS".equalsIgnoreCase(catName)) {
                weight = toyRepo.findByProductId(product.getId()).map(ToyDetailDynamoDB::getWeight).orElse(BigDecimal.ZERO);
            } else if ("FURNITURE".equalsIgnoreCase(catName)) {
                weight = furnitureRepo.findByProductId(product.getId()).map(FurnitureDetailDynamoDB::getWeight).orElse(BigDecimal.ZERO);
            }
            
            totalWeight += weight.intValue() * item.getQuantity();
        }

        ghnRequest.setItems(ghnItems);
        ghnRequest.setWeight(totalWeight > 0 ? totalWeight : 200);
        
        // Các thông số mặc định khác
        ghnRequest.setLength(20);
        ghnRequest.setWidth(20);
        ghnRequest.setHeight(10);
        ghnRequest.setInsurance_value(codAmount.intValue());
        ghnRequest.setService_id(53321);
        ghnRequest.setPayment_type_id(2);
        ghnRequest.setNote("Ghi chú từ đơn hàng");
        ghnRequest.setRequired_note("CHOXEMHANGKHONGTHU");

        return ghnRequest;
    }
}