package com.urlshortener.dto;

import java.time.LocalDateTime;

public class UrlAnalyticsResponse {

    private String shortCode;
    private String shortUrl;
    private String longUrl;
    private long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;

    public UrlAnalyticsResponse(String shortCode, String shortUrl, String longUrl, long clickCount,
                                LocalDateTime createdAt, LocalDateTime lastAccessedAt) {
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
        this.lastAccessedAt = lastAccessedAt;
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

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }
}
