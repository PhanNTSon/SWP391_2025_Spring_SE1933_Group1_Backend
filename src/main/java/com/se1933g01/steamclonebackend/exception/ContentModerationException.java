package com.se1933g01.steamclonebackend.exception;

public class ContentModerationException extends RuntimeException {
    public ContentModerationException(String message) {
        super(message);
    }

    public ContentModerationException(String message, Throwable cause) {
        super(message, cause);
    }
}