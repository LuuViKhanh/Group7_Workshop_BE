package org.example.flyora_backend.service;

import org.example.flyora_backend.DTOs.ChangePasswordDTO;
import org.example.flyora_backend.DTOs.ProfileDTO;
import org.example.flyora_backend.DTOs.UpdateProfileDTO;
import org.example.flyora_backend.dynamo.models.AccountDynamoDB;

public interface ProfileService {
    ProfileDTO getProfile(AccountDynamoDB account);

    void updateProfile(AccountDynamoDB account, UpdateProfileDTO request);

    void changePassword(AccountDynamoDB account, ChangePasswordDTO request);
}
