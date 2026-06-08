package com.se1933g01.steamclonebackend.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.user.SessionLog;

@Repository
public interface SessionLogRepo extends JpaRepository<SessionLog, Long> {
    @Query(value = "SELECT COUNT(DISTINCT \"UserID\") FROM \"SessionLog\" WHERE DATE(\"LoginTime\") = :date", nativeQuery = true)
    int countLoginsByDate(@Param("date") LocalDate date);
@Query(value = """
    SELECT DATE("LoginTime") AS label, COUNT(DISTINCT "UserID") AS total
    FROM "SessionLog"
    WHERE "LoginTime" >= :startDate
    GROUP BY DATE("LoginTime")
    ORDER BY DATE("LoginTime") ASC
""", nativeQuery = true)
List<Object[]> fetchLoginsSince(@Param("startDate") LocalDateTime startDate);
}
