package com.urlshortener.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    private final boolean success;
    private final String message;
    private final int status;
    private final Map<String, String> errors;
    private final LocalDateTime timestamp;

    public ErrorResponse(boolean success, String message, int status, Map<String, String> errors, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.status = status;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public static ErrorResponse of(String message, int status, Map<String, String> errors) {
        return new ErrorResponse(false, message, status, errors, LocalDateTime.now());
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
