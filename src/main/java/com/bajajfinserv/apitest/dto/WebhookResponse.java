// File: src/main/java/com/bajajfinserv/apitest/dto/WebhookResponse.java
package com.bajajfinserv.apitest.dto;

public class WebhookResponse {
    private String webhook;
    private String accessToken;

    // Getters and Setters
    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}