package org.example.flyora_backend.service;

import org.example.flyora_backend.DTOs.ChangePasswordDTO;
import org.example.flyora_backend.DTOs.ProfileDTO;
import org.example.flyora_backend.DTOs.UpdateProfileDTO;
import org.example.flyora_backend.dynamo.models.AccountDynamoDB;
import org.example.flyora_backend.dynamo.models.RoleDynamoDB;
import org.example.flyora_backend.repository.AccountRepository;
import org.example.flyora_backend.repository.AdminRepository;
import org.example.flyora_backend.repository.CustomerRepository;
import org.example.flyora_backend.repository.RoleRepository; // Inject thêm
import org.example.flyora_backend.repository.ShopOwnerRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

    private final CustomerRepository customerRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository; // Inject RoleRepo

    @Override
    public ProfileDTO getProfile(AccountDynamoDB account) {
        ProfileDTO dto = new ProfileDTO();
        dto.setAccountId(account.getId());
        dto.setUsername(account.getUsername());
        dto.setEmail(account.getEmail());
        dto.setPhone(account.getPhone());

        // SỬA LỖI: Lấy Role Name thủ công
        String roleName = roleRepository.findById(account.getRoleId())
                .map(RoleDynamoDB::getName).orElse("UNKNOWN");
        dto.setRoleName(roleName);

        switch (roleName) {
            case "Customer" -> customerRepository.findByAccountId(account.getId())
                .ifPresent(c -> dto.setName(c.getName()));
            case "ShopOwner" -> shopOwnerRepository.findByAccountId(account.getId())
                .ifPresent(o -> dto.setName(o.getName()));
            case "Admin" -> adminRepository.findByAccountId(account.getId())
                .ifPresent(a -> dto.setName(a.getName()));
        }

        return dto;
    }

    @Override
    public void updateProfile(AccountDynamoDB account, UpdateProfileDTO request) {
        account.setEmail(request.getEmail());
        account.setPhone(request.getPhone());
        accountRepository.save(account);

        // SỬA LỖI: Lấy Role Name thủ công
        String roleName = roleRepository.findById(account.getRoleId())
                .map(RoleDynamoDB::getName).orElse("UNKNOWN");

        switch (roleName) {
            case "Customer" -> customerRepository.findByAccountId(account.getId())
                .ifPresent(c -> {
                    c.setName(request.getName());
                    customerRepository.save(c);
                });
            case "ShopOwner" -> shopOwnerRepository.findByAccountId(account.getId())
                .ifPresent(s -> {
                    s.setName(request.getName());
                    shopOwnerRepository.save(s);
                });
            case "Admin" -> adminRepository.findByAccountId(account.getId())
                .ifPresent(a -> {
                    a.setName(request.getName());
                    adminRepository.save(a);
                });
        }
    }

    @Override
    public void changePassword(AccountDynamoDB account, ChangePasswordDTO request) {
        if (!account.getPassword().equals(request.getCurrentPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        if (request.getNewPassword().length() > 0) {
            // Logic check độ dài nên cụ thể hơn, ví dụ > 6
            if (request.getNewPassword().length() < 6) throw new RuntimeException("Mật khẩu quá ngắn"); 
        }

        account.setPassword(request.getNewPassword());
        accountRepository.save(account);
    }
}