package com.se1933g01.steam_clone_backend.repository;
import com.se1933g01.steam_clone_backend.entity.request.PublisherApplyRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherApplyRequestRepo extends JpaRepository<PublisherApplyRequest, Long> {
    @Query("SELECT agr FROM PublisherApplyRequest agr JOIN Request r ON agr.requestId = r.requestId WHERE r.requestState = 0")
    Page<PublisherApplyRequest> findAllByRequestStateZero(Pageable pageable);
}
