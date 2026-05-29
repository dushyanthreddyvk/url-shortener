package com.urlshortener.service;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.UrlAnalyticsResponse;
import com.urlshortener.dto.UrlResponse;

public interface UrlService {

    UrlResponse shortenUrl(ShortenUrlRequest request);

    String getLongUrlAndTrackClick(String shortCode);

    UrlResponse getUrlDetails(String shortCode);

    UrlAnalyticsResponse getAnalytics(String shortCode);
}
