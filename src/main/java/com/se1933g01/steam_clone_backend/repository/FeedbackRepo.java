package com.se1933g01.steam_clone_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.se1933g01.steam_clone_backend.entity.request.Feedback;

public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
    @Query("SELECT agr FROM Feedback agr JOIN Request r ON agr.requestId = r.requestId WHERE r.requestState = 0")
    Page<Feedback> findAllByRequestStateZero(Pageable pageable);
}
