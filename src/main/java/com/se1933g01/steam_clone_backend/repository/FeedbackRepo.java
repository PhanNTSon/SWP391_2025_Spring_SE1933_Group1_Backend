package com.se1933g01.steam_clone_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se1933g01.steam_clone_backend.entity.request.Feedback;

public interface FeedbackRepo extends JpaRepository<Feedback, Long> {

}
