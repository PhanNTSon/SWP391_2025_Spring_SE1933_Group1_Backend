package com.se1933g01.steamclonebackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steamclonebackend.dto.LoginChartDTO;
import com.se1933g01.steamclonebackend.service.DashboardService;
import java.util.List;
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    @GetMapping("/chart")
    public ResponseEntity<List<LoginChartDTO>> getChart(@RequestParam String mode) {
        int days = switch (mode.toLowerCase()) {
            case "month" -> 30;
            case "week" -> 7;
            default -> 7;
        };
        return ResponseEntity.ok(dashboardService.getLoginsForDays(days));
    }
}
