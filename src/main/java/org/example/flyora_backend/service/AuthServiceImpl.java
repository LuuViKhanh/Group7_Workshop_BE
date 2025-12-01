package org.example.flyora_backend.service;

import java.util.Map;

import org.example.flyora_backend.DTOs.LoginDTO;
import org.example.flyora_backend.DTOs.LoginResponseDTO;
import org.example.flyora_backend.DTOs.RegisterDTO;
import org.example.flyora_backend.dynamo.models.*;
import org.example.flyora_backend.repository.AccountRepository;
import org.example.flyora_backend.repository.AdminRepository;
import org.example.flyora_backend.repository.CustomerRepository;
import org.example.flyora_backend.repository.RoleRepository;
import org.example.flyora_backend.repository.SalesStaffRepository;
import org.example.flyora_backend.repository.ShopOwnerRepository;
import org.example.flyora_backend.Utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final AdminRepository adminRepository;
    private final SalesStaffRepository salesStaffRepository;
    private final JwtUtil jwtUtil;
    private final AccessLogService accessLogService;

    @Override
    public Map<String, Object> registerCustomer(RegisterDTO request) {
        if (accountRepository.existsByUsername(request.getUsername()) == 1) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }

        RoleDynamoDB customerRole = roleRepository.findByName("Customer")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại"));

        AccountDynamoDB account = new AccountDynamoDB();
        account.setId(accountRepository.generateNewId());
        account.setUsername(request.getUsername());
        account.setPassword(request.getPassword());
        account.setEmail(request.getEmail());
        account.setPhone(request.getPhone());
        account.setRoleId(customerRole.getId());
        account.setIsActive(1);   // dùng 1 thay cho true
        account.setIsApproved(1); // dùng 1 thay cho true
        accountRepository.save(account);

        CustomerDynamoDB customer = new CustomerDynamoDB();
        customer.setId(customerRepository.generateNewId());
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setAccountId(account.getId());
        customerRepository.save(customer);

        return Map.of("message", "Đăng ký thành công", "userId", customer.getId());
    }

    @Override
    public LoginResponseDTO loginCustomer(LoginDTO request) {
        AccountDynamoDB account = accountRepository.findByUsername(request.getUsername())
                .filter(acc -> acc.getPassword().equals(request.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai thông tin"));

        if (account.getIsActive() != 1 || account.getIsApproved() != 1) {
            throw new RuntimeException("Tài khoản chưa kích hoạt");
        }

        String roleName = roleRepository.findById(account.getRoleId()).map(RoleDynamoDB::getName).orElse("UNKNOWN");

        LoginResponseDTO response = new LoginResponseDTO();
        response.setUserId(account.getId());
        response.setName(account.getUsername());
        response.setRole(roleName);

        switch (roleName) {
            case "Customer" -> customerRepository.findByAccountId(account.getId())
                    .ifPresent(c -> { response.setName(c.getName()); response.setLinkedId(c.getId()); });
            case "ShopOwner" -> shopOwnerRepository.findByAccountId(account.getId())
                    .ifPresent(s -> { response.setName(s.getName()); response.setLinkedId(s.getId()); });
            case "Admin" -> adminRepository.findByAccountId(account.getId())
                    .ifPresent(a -> { response.setName(a.getName()); response.setLinkedId(a.getId()); });
            case "SalesStaff" -> salesStaffRepository.findByAccountId(account.getId())
                    .ifPresent(s -> { response.setName(s.getName()); response.setLinkedId(s.getId()); });
        }

        String token = jwtUtil.generateToken(account); 
        response.setToken(token);
        accessLogService.logAction(account.getId(), "Đăng nhập thành công");
        return response;
    }
}
