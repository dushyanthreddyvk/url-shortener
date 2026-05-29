package com.urlshortener.controller;

import com.urlshortener.dto.ApiResponse;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.UrlAnalyticsResponse;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class UrlController {

    private static final Logger log = LoggerFactory.getLogger(UrlController.class);

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/urls/shorten")
    public ResponseEntity<ApiResponse<UrlResponse>> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        UrlResponse response = urlService.shortenUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Short URL generated successfully", response));
    }

    @GetMapping("/{shortCode:[A-Za-z0-9]{7}}")
    public void redirectToOriginalUrl(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        String longUrl = urlService.getLongUrlAndTrackClick(shortCode);
        log.info("Redirecting shortCode={} to longUrl={}", shortCode, longUrl);
        response.sendRedirect(longUrl);
    }

    @GetMapping("/api/urls/{shortCode}")
    public ResponseEntity<ApiResponse<UrlResponse>> getUrlDetails(@PathVariable String shortCode) {
        UrlResponse response = urlService.getUrlDetails(shortCode);
        return ResponseEntity.ok(ApiResponse.success("URL details fetched successfully", response));
    }

    @GetMapping("/api/urls/{shortCode}/analytics")
    public ResponseEntity<ApiResponse<UrlAnalyticsResponse>> getAnalytics(@PathVariable String shortCode) {
        UrlAnalyticsResponse response = urlService.getAnalytics(shortCode);
        return ResponseEntity.ok(ApiResponse.success("URL analytics fetched successfully", response));
    }
}
