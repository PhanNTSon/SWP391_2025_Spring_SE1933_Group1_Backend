package com.se1933g01.steam_clone_backend.repository;

import com.se1933g01.steam_clone_backend.entity.request.PublisherApplyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherApplyRequestRepo extends JpaRepository<PublisherApplyRequest, Long> {
}
