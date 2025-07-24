package com.se1933g01.steamclonebackend.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.dto.LoginChartDTO;
import com.se1933g01.steamclonebackend.repository.PublisherRepo;
import com.se1933g01.steamclonebackend.repository.RequestRepo;
import com.se1933g01.steamclonebackend.repository.SessionLogRepo;
import com.se1933g01.steamclonebackend.repository.TransactionRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;
@Service
public class DashboardService {
    private final SessionLogRepo sessionLogRepo;
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;
    private final PublisherRepo publisherRepo;
    private final RequestRepo requestRepo;
    public DashboardService(SessionLogRepo sessionLogRepo, TransactionRepo transactionRepo, UserRepo userRepo, PublisherRepo publisherRepo, RequestRepo requestRepo) {
        this.sessionLogRepo = sessionLogRepo;
        this.transactionRepo = transactionRepo;
        this.userRepo = userRepo;
        this.publisherRepo = publisherRepo;
        this.requestRepo = requestRepo;
    }
    public List<LoginChartDTO> getLoginsForDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(days - 1);
        Map<LocalDate, Integer> statsMap = new HashMap<>();

        List<Object[]> rawData = sessionLogRepo.fetchLoginsSince(start.atStartOfDay());
        for (Object[] row : rawData) {
            java.sql.Date sqlDate = (java.sql.Date) row[0];
            LocalDate date = sqlDate.toLocalDate();
            int total = ((Number) row[1]).intValue();
            statsMap.put(date, total);
        }

        return IntStream.range(0, days)
            .mapToObj(i -> {
                LocalDate date = start.plusDays(i);
                return new LoginChartDTO(date, statsMap.getOrDefault(date, 0));
            })
            .toList();
    }

    public double getCurrentMonthRevenue() {
        return transactionRepo.getMonthlyRevenue() != null
            ? transactionRepo.getMonthlyRevenue()
            : 0.0;
    }
    public long getTotalUserCount() {
        return userRepo.countAllUsers();
    }
    public long getTotalPublisherCount() {
        return publisherRepo.countAllPublishers();
    }
    public long getTotalPendingRequests() {
        return requestRepo.countAllPendingRequests();
    }
    public double getCurrentMonthRefund() {
        return transactionRepo.getMonthlyRefund() != null
            ? transactionRepo.getMonthlyRefund()
            : 0.0;
    }
}

