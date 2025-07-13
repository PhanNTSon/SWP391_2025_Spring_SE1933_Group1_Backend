package com.se1933g01.steamclonebackend.exception;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.se1933g01.steamclonebackend.dto.ApiRespDTO;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InappropriateContentException.class)
    public ResponseEntity<ApiRespDTO<?>> handleInappropriateContentException(InappropriateContentException ex) {
        ApiRespDTO<?> resp = new ApiRespDTO<>(false, "UNPROCESSABLE_ENTITY", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resp);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiRespDTO<?>> handleEntityNotFoundException(EntityNotFoundException ex) {
        ApiRespDTO<?> resp = new ApiRespDTO<>(false, "ENTITY_NOT_FOUND", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @ExceptionHandler(BeanInstantiationException.class)
    public ResponseEntity<ApiRespDTO<?>> handleBeanError(BeanInstantiationException ex) {
        ApiRespDTO<?> resp = new ApiRespDTO<>(false, "BEAN_INSTANCE_INITIATE_FAIL", ex.getMessage(), null);
        return ResponseEntity.status(500).body(resp);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiRespDTO<?>> handleStateError(IllegalStateException ex) {
        ApiRespDTO<?> resp = new ApiRespDTO<>(false, "ILLEGAL_STATE_OR_LOGIC", ex.getMessage(), null);
        return ResponseEntity.status(500).body(resp);
    }

    @ExceptionHandler(ContentModerationException.class)
    public ResponseEntity<ApiRespDTO<?>> handleModerationError(ContentModerationException ex) {
        ApiRespDTO<?> response = new ApiRespDTO<>(
                false,
                "MODERATION_FAILED",
                "Failed to check content moderation. Please try again later.",
                null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiRespDTO<?>> handleArgumentError(IllegalArgumentException ex) {
        ApiRespDTO<?> resp = new ApiRespDTO<>(false, "ILLEGAL_ARGUMENT", ex.getMessage(), null);
        return ResponseEntity.status(500).body(resp);
    }
}
