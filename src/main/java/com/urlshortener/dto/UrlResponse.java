package com.urlshortener.dto;

import java.time.LocalDateTime;

public class UrlResponse {

    private String shortCode;
    private String shortUrl;
    private String longUrl;
    private long clickCount;
    private LocalDateTime createdAt;

    public UrlResponse(String shortCode, String shortUrl, String longUrl, long clickCount, LocalDateTime createdAt) {
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public long getClickCount() {
        return clickCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
