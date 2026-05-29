package com.urlshortener.service;

import com.urlshortener.config.AppProperties;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UrlServiceImplTest {

    private TestRepository repository;
    private FixedShortCodeGenerator shortCodeGenerator;
    private UrlServiceImpl urlService;

    @BeforeEach
    void setUp() {
        repository = new TestRepository();
        shortCodeGenerator = new FixedShortCodeGenerator("Abc123x");
        AppProperties appProperties = new AppProperties();
        appProperties.setBaseUrl("http://localhost:8080");
        urlService = new UrlServiceImpl(repository.proxy(), shortCodeGenerator, appProperties);
    }

    @Test
    void shortenUrlCreatesNewMappingWhenLongUrlDoesNotExist() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("https://example.com/page");

        repository.longUrlResult = Optional.empty();
        repository.shortCodeExists = false;

        UrlResponse response = urlService.shortenUrl(request);

        assertThat(response.getShortCode()).isEqualTo("Abc123x");
        assertThat(response.getShortUrl()).isEqualTo("http://localhost:8080/Abc123x");
        assertThat(response.getLongUrl()).isEqualTo("https://example.com/page");
    }

    @Test
    void shortenUrlReturnsExistingMappingForDuplicateLongUrl() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("https://example.com/page");
        UrlMapping existing = mapping("https://example.com/page", "Old123x");

        repository.longUrlResult = Optional.of(existing);

        UrlResponse response = urlService.shortenUrl(request);

        assertThat(response.getShortCode()).isEqualTo("Old123x");
        assertThat(response.getShortUrl()).isEqualTo("http://localhost:8080/Old123x");
    }

    @Test
    void getLongUrlAndTrackClickIncrementsClickCount() {
        UrlMapping existing = mapping("https://example.com/page", "Clk123x");
        repository.shortCodeResult = Optional.of(existing);

        String longUrl = urlService.getLongUrlAndTrackClick("Clk123x");

        assertThat(longUrl).isEqualTo("https://example.com/page");
        assertThat(existing.getClickCount()).isEqualTo(1);
        assertThat(existing.getLastAccessedAt()).isNotNull();
        assertThat(repository.saveCalled).isTrue();
    }

    @Test
    void getUrlDetailsThrowsWhenShortCodeDoesNotExist() {
        repository.shortCodeResult = Optional.empty();

        assertThatThrownBy(() -> urlService.getUrlDetails("missing"))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessageContaining("missing");
    }

    private UrlMapping mapping(String longUrl, String shortCode) {
        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl(longUrl);
        mapping.setShortCode(shortCode);
        return mapping;
    }

    private static class FixedShortCodeGenerator extends ShortCodeGenerator {

        private final String value;

        FixedShortCodeGenerator(String value) {
            this.value = value;
        }

        @Override
        public String generate() {
            return value;
        }
    }

    private static class TestRepository implements InvocationHandler {

        private Optional<UrlMapping> longUrlResult = Optional.empty();
        private Optional<UrlMapping> shortCodeResult = Optional.empty();
        private boolean shortCodeExists;
        private boolean saveCalled;

        UrlMappingRepository proxy() {
            return (UrlMappingRepository) Proxy.newProxyInstance(
                    UrlMappingRepository.class.getClassLoader(),
                    new Class<?>[]{UrlMappingRepository.class},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "findByLongUrl" -> longUrlResult;
                case "findByShortCode" -> shortCodeResult;
                case "existsByShortCode" -> shortCodeExists;
                case "save" -> {
                    saveCalled = true;
                    yield args[0];
                }
                default -> throw new UnsupportedOperationException("Unsupported repository method: " + method.getName());
            };
        }
    }
}
