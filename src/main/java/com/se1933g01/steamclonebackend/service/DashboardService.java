package com.se1933g01.steamclonebackend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.dto.LoginChartDTO;
import com.se1933g01.steamclonebackend.repository.SessionLogRepo;
@Service
public class DashboardService {
    private final SessionLogRepo sessionLogRepo;

    public DashboardService(SessionLogRepo sessionLogRepo) {
        this.sessionLogRepo = sessionLogRepo;
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


}
