package com.se1933g01.steamclonebackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.se1933g01.steamclonebackend.entity.request.Feedback;

public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
    @Query("SELECT agr FROM Feedback agr JOIN Request r ON agr.requestId = r.requestId WHERE r.requestState = 0")
    Page<Feedback> findAllByRequestStateZero(Pageable pageable);
    @Query("SELECT u.username FROM Feedback f JOIN f.request r JOIN r.user u WHERE r.requestId = :requestId")
    // @Query("SELECT new com.yourpackage.UserSummary(u.username, u.userId) FROM Feedback f JOIN f.request r JOIN r.user u WHERE r.requestId = :requestId")
    String findUserNameByRequestId(@Param("requestId") Long requestId);
    @Query("SELECT u.userId FROM Feedback f JOIN f.request r JOIN r.user u WHERE r.requestId = :requestId")
    String findUserIdByRequestId(@Param("requestId") Long requestId);

}
