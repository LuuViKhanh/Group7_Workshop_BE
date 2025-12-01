package org.example.flyora_backend.DTOs;

public class WebhookURL {
    private String webhookUrl;

    public WebhookURL() {}

    public WebhookURL(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
}
