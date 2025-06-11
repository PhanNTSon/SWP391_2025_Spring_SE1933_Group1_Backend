package com.se1933g01.steam_clone_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steam_clone_backend.entity.request.AddingGameRequest;
@Repository
public interface AddingGameRequestRepo extends JpaRepository<AddingGameRequest,Long> {
    @Query("SELECT agr FROM AddingGameRequest agr JOIN Request r ON agr.requestId = r.requestId WHERE r.requestState = 0")
    Page<AddingGameRequest> findAllByRequestStateZero(Pageable pageable);
    @Query("SELECT p.publisherName FROM AddingGameRequest agr " +
       "JOIN agr.request r " +
       "JOIN r.user u " +
       "JOIN u.publisher p " +
       "WHERE agr.requestId = :requestId")
    String findPublisherNameByRequestId(@Param("requestId") Long requestId);
    
}
