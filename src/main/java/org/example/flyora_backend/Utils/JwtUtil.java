package org.example.flyora_backend.Utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

// 1. Import Model DynamoDB
import org.example.flyora_backend.dynamo.models.AccountDynamoDB;
import org.example.flyora_backend.dynamo.models.RoleDynamoDB;
import org.example.flyora_backend.repository.AccountRepository;
import org.example.flyora_backend.repository.RoleRepository; // 2. Import Role Repo

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${APP_JWT_SECRET}")
    private String secret;

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository; // 3. Inject RoleRepo để lấy tên Role

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 4. Đổi tham số đầu vào thành AccountDynamoDB
    public String generateToken(AccountDynamoDB account) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 86400000); 

        // 5. Lấy tên Role thủ công (Manual Lookup)
        String roleName = "UNKNOWN";
        if (account.getRoleId() != null) {
            roleName = roleRepository.findById(account.getRoleId())
                    .map(RoleDynamoDB::getName)
                    .orElse("UNKNOWN");
        }

        return Jwts.builder()
                .setSubject(account.getUsername())
                .claim("id", account.getId())
                .claim("role", roleName) // Sử dụng tên role vừa tìm được
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 6. Đổi kiểu trả về thành AccountDynamoDB
    public AccountDynamoDB getAccountFromToken(String token) {
        String username = getUsernameFromToken(token);
        // Hàm findByUsername cần được implement trong AccountRepository (dùng GSI)
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        return claims.getSubject();
    }
}