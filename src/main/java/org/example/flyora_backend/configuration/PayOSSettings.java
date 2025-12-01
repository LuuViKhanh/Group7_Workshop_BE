package org.example.flyora_backend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payos")
public class PayOSSettings {
    private String clientId;
    private String apiKey;
    private String checksumKey;
    private String baseUrl;

    // Getters and Setters
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getChecksumKey() { return checksumKey; }
    public void setChecksumKey(String checksumKey) { this.checksumKey = checksumKey; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
}

