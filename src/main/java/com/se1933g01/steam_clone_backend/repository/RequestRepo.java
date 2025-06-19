package com.se1933g01.steam_clone_backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steam_clone_backend.entity.request.Request;
@Repository
public interface RequestRepo extends JpaRepository<Request,Long> {
    @Modifying
    @Query(value = "DELETE FROM request WHERE requestid = :id", nativeQuery = true)
    void deleteById(@Param("id") Long id);


}
