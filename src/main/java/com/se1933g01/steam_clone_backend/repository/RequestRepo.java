package com.se1933g01.steam_clone_backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.se1933g01.steam_clone_backend.entity.request.Request;
@Repository
public interface RequestRepo extends JpaRepository<Request,Long> {

    
}
