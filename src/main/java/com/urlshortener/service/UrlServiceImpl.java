package com.urlshortener.service;

import com.urlshortener.config.AppProperties;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.UrlAnalyticsResponse;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlServiceImpl implements UrlService {

    private static final Logger log = LoggerFactory.getLogger(UrlServiceImpl.class);
    private static final int MAX_CODE_GENERATION_ATTEMPTS = 10;

    private final UrlMappingRepository urlMappingRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final AppProperties appProperties;

    public UrlServiceImpl(UrlMappingRepository urlMappingRepository,
                          ShortCodeGenerator shortCodeGenerator,
                          AppProperties appProperties) {
        this.urlMappingRepository = urlMappingRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.appProperties = appProperties;
    }

    @Override
    @Transactional
    public UrlResponse shortenUrl(ShortenUrlRequest request) {
        String normalizedLongUrl = request.getLongUrl().trim();

        return urlMappingRepository.findByLongUrl(normalizedLongUrl)
                .map(this::toUrlResponse)
                .orElseGet(() -> createUrlMapping(normalizedLongUrl));
    }

    @Override
    @Transactional
    public String getLongUrlAndTrackClick(String shortCode) {
        UrlMapping mapping = findMapping(shortCode);
        mapping.incrementClickCount();
        UrlMapping saved = urlMappingRepository.save(mapping);
        log.info("Tracked click for shortCode={}, clickCount={}", shortCode, saved.getClickCount());
        return saved.getLongUrl();
    }

    @Override
    @Transactional(readOnly = true)
    public UrlResponse getUrlDetails(String shortCode) {
        return toUrlResponse(findMapping(shortCode));
    }

    @Override
    @Transactional(readOnly = true)
    public UrlAnalyticsResponse getAnalytics(String shortCode) {
        UrlMapping mapping = findMapping(shortCode);
        return new UrlAnalyticsResponse(
                mapping.getShortCode(),
                buildShortUrl(mapping.getShortCode()),
                mapping.getLongUrl(),
                mapping.getClickCount(),
                mapping.getCreatedAt(),
                mapping.getLastAccessedAt()
        );
    }

    private UrlResponse createUrlMapping(String longUrl) {
        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl(longUrl);
        mapping.setShortCode(generateUniqueShortCode());

        UrlMapping saved = urlMappingRepository.save(mapping);
        log.info("Created URL mapping shortCode={} longUrl={}", saved.getShortCode(), saved.getLongUrl());
        return toUrlResponse(saved);
    }

    private String generateUniqueShortCode() {
        for (int attempt = 0; attempt < MAX_CODE_GENERATION_ATTEMPTS; attempt++) {
            String shortCode = shortCodeGenerator.generate();
            if (!urlMappingRepository.existsByShortCode(shortCode)) {
                return shortCode;
            }
        }
        throw new IllegalStateException("Unable to generate unique short code");
    }

    private UrlMapping findMapping(String shortCode) {
        return urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
    }

    private UrlResponse toUrlResponse(UrlMapping mapping) {
        return new UrlResponse(
                mapping.getShortCode(),
                buildShortUrl(mapping.getShortCode()),
                mapping.getLongUrl(),
                mapping.getClickCount(),
                mapping.getCreatedAt()
        );
    }

    private String buildShortUrl(String shortCode) {
        return appProperties.getBaseUrl().replaceAll("/+$", "") + "/" + shortCode;
    }
}
