package com.se1933g01.steamclonebackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steamclonebackend.dto.DashboardDTO;
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
    @GetMapping("/revenue")
    public ResponseEntity<DashboardDTO> getMonthlyRevenue() {
        double revenue = dashboardService.getCurrentMonthRevenue();
        return ResponseEntity.ok(new DashboardDTO("Revenue", revenue));
    }
    @GetMapping("/users/count")
    public ResponseEntity<DashboardDTO> getTotalUserCount() {
        long userCount = dashboardService.getTotalUserCount();
        return ResponseEntity.ok(new DashboardDTO("Total Users", userCount));
    }
    @GetMapping("/publishers/count")
    public ResponseEntity<DashboardDTO> getTotalPublisherCount() {
        long publisherCount = dashboardService.getTotalPublisherCount();
        return ResponseEntity.ok(new DashboardDTO("Total Publishers", publisherCount));
    }
    @GetMapping("/requests/pending/count")
    public ResponseEntity<DashboardDTO> getTotalPendingRequests() {
        long pendingRequests = dashboardService.getTotalPendingRequests();
        return ResponseEntity.ok(new DashboardDTO("Total Pending Requests", pendingRequests));
    }
    @GetMapping("/transactions/refund/count")
    public ResponseEntity<DashboardDTO> getMonthlyRefund() {
        double refund = dashboardService.getCurrentMonthRefund();
        return ResponseEntity.ok(new DashboardDTO("Refund rate", refund));
    }
}
