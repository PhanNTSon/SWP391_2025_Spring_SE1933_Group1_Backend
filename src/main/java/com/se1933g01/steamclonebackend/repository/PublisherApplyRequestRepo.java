package com.se1933g01.steamclonebackend.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.request.PublisherApplyRequest;

@Repository
public interface PublisherApplyRequestRepo extends JpaRepository<PublisherApplyRequest, Long> {
    @Query("SELECT agr FROM PublisherApplyRequest agr JOIN Request r ON agr.requestId = r.requestId WHERE r.requestState = 0")
    Page<PublisherApplyRequest> findAllByRequestStateZero(Pageable pageable);
    @Query("SELECT agr FROM PublisherApplyRequest agr JOIN Request r ON agr.requestId = r.requestId WHERE r.user.userId = :userId AND r.requestState = 0 OR r.requestState = 2")
    PublisherApplyRequest findByUserIdAndRequestStateZeroAndTwo(long userId);
    @Query("SELECT u.userId FROM PublisherApplyRequest agr " +
    "JOIN agr.request r " +
    "JOIN r.user u " +
    "WHERE agr.requestId = :requestId")
    String findUserIdByRequestId(@Param("requestId") Long requestId);
}
