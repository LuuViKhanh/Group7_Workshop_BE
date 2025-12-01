package org.example.flyora_backend.service;

import lombok.RequiredArgsConstructor;
import org.example.flyora_backend.DTOs.AccountDTO;
import org.example.flyora_backend.DTOs.UserDTO;
import org.example.flyora_backend.dynamo.models.*;
import org.example.flyora_backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final SalesStaffRepository salesStaffRepository;
    // Các repo phụ để xóa cascade
    private final AccessLogRepository accessLogRepository;
    private final NotificationRepository notificationRepository;
    private final ProductReviewRepository productReviewRepository;
    private final ChatBotRepository chatBotRepository;
    private final IssueReportRepository issueReportRepository;
    private final PromotionRepository promotionRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final DeliveryNoteRepository deliveryNoteRepository;
    private final InventoryRepository inventoryRepository;
    private final SystemLogRepository systemLogRepository;
    private final FaqRepository faqRepository;
    private final PolicyRepository policyRepository;

    public AccountDynamoDB createAccount(AccountDTO dto) {

        // 1. Validate Role
        RoleDynamoDB role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role"));

        // 2. Tạo Account với ID INTEGER
        Integer newAccId = accountRepository.generateNewId();

        AccountDynamoDB acc = new AccountDynamoDB();
        acc.setId(newAccId);  // Integer
        acc.setUsername(dto.getUsername());
        acc.setPassword(dto.getPassword());
        acc.setPhone(dto.getPhone());
        acc.setEmail(dto.getEmail());
        acc.setRoleId(role.getId());
        acc.setIsActive(1);
        acc.setIsApproved(1);

        if (dto.getApprovedBy() != null) {
            acc.setApprovedBy(dto.getApprovedBy());
        }

        accountRepository.save(acc);

        // 3. Entity con
        switch (role.getName()) {
            case "Admin" -> {
                AdminDynamoDB admin = new AdminDynamoDB();
                admin.setId(adminRepository.generateNewId());
                admin.setName(dto.getName());
                admin.setAccountId(newAccId);
                adminRepository.save(admin);
            }
            case "Customer" -> {
                CustomerDynamoDB customer = new CustomerDynamoDB();
                customer.setId(customerRepository.generateNewId());
                customer.setName(dto.getName());
                customer.setEmail(acc.getEmail());
                customer.setAccountId(newAccId);
                customerRepository.save(customer);
            }
            case "ShopOwner" -> {
                ShopOwnerDynamoDB owner = new ShopOwnerDynamoDB();
                owner.setId(shopOwnerRepository.generateNewId());
                owner.setName(dto.getName());
                owner.setAccountId(newAccId);
                shopOwnerRepository.save(owner);
            }
            case "SalesStaff" -> {
                SalesStaffDynamoDB staff = new SalesStaffDynamoDB();
                staff.setId(salesStaffRepository.generateNewId());
                staff.setName(dto.getName());
                staff.setAccountId(newAccId);
                salesStaffRepository.save(staff);
            }
            default -> throw new RuntimeException("Role không hợp lệ");
        }

        return acc;
    }


    public List<UserDTO> getAllAccounts() {
        // N+1 Problem: Lấy tất cả account, sau đó loop lấy Role name
        // Với DynamoDB, chấp nhận việc này hoặc cache Role trong memory
        return accountRepository.findAll()
                .stream()
                .map(acc -> {
                    String roleName = roleRepository.findById(acc.getRoleId())
                            .map(RoleDynamoDB::getName).orElse("Unknown");
                    return new UserDTO(acc, roleName);
                })
                .collect(Collectors.toList());
    }

    public AccountDynamoDB updateAccount(Integer id, AccountDTO dto) {
        AccountDynamoDB acc = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        acc.setUsername(dto.getUsername());
        acc.setPassword(dto.getPassword());
        acc.setPhone(dto.getPhone());
        acc.setEmail(dto.getEmail());
        if (dto.getRoleId() != null) acc.setRoleId(dto.getRoleId());
        if (dto.getApprovedBy() != null) acc.setApprovedBy(dto.getApprovedBy());
        acc.setIsActive(dto.getIsActive());
        acc.setIsApproved(dto.getIsApproved());

        accountRepository.save(acc);
        return acc;
    }

    public void deleteAccount(Integer id) {
        AccountDynamoDB account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        String roleName = roleRepository.findById(account.getRoleId()).map(RoleDynamoDB::getName).orElse("");

        // 1. Xóa Log & Noti
        accessLogRepository.deleteByAccountId(id);
        notificationRepository.deleteByRecipientId(id);

        // 2. Logic Cascade thủ công
        if ("Customer".equals(roleName)) {
            CustomerDynamoDB customer = customerRepository.findByAccountId(id)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            Integer cId = customer.getId();

            productReviewRepository.deleteByCustomerId(cId);
            chatBotRepository.deleteById(id);// Repo này phải implement deleteByCustomerId
            issueReportRepository.deleteByCustomerId(cId);
            promotionRepository.deleteByCustomerId(cId);
            paymentRepository.deleteByCustomerId(cId);

            List<OrderDynamoDB> orders = orderRepository.findByCustomerId(cId);
            for (OrderDynamoDB o : orders) {
                orderItemRepository.deleteByOrderId(o.getId());
                deliveryNoteRepository.deleteByOrderId(o.getId());
                orderRepository.deleteById(o.getId());
            }
            customerRepository.deleteById(cId);

        } else if ("ShopOwner".equals(roleName)) {
            shopOwnerRepository.deleteById(id);
        } else if ("SalesStaff".equals(roleName)) {
            inventoryRepository.deleteBySalesStaffAccountId(id);
            salesStaffRepository.deleteById(id);
        } else if ("Admin".equals(roleName)) {
            systemLogRepository.deleteById(id); // Cần thêm method này trong SystemLogRepo
            faqRepository.clearUpdatedBy(id);
            policyRepository.clearUpdatedBy(id);
            adminRepository.deleteById(id); // Cần method deleteByAccountId
        }

        accountRepository.deleteById(id);
    }

    public AccountDynamoDB setActiveStatus(Integer id, Integer isActive) {
        AccountDynamoDB acc = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        acc.setIsActive(isActive);
        accountRepository.save(acc);

        return acc;
    }


}