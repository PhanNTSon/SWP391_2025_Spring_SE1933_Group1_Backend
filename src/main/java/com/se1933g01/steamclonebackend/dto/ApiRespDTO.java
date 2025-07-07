package com.se1933g01.steamclonebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiRespDTO<T> {
    private boolean success;
    private String code; // e.g. "USER_NOT_FOUND", "VALIDATION_FAILED"
    private String message; // e.g. "User not found with ID 5"
    private T data;

}
