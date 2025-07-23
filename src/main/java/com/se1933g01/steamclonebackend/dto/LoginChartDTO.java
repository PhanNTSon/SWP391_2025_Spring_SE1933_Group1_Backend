package com.se1933g01.steamclonebackend.dto;

import java.time.LocalDate;

import lombok.Data;
@Data
public class LoginChartDTO {
    private LocalDate label;
    private int total;

    public LoginChartDTO(LocalDate label, int total) {
        this.label = label;
        this.total = total;
    }
}
