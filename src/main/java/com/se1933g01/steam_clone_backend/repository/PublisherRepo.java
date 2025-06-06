package com.se1933g01.steam_clone_backend.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.se1933g01.steam_clone_backend.entity.user.Publisher;

public interface PublisherRepo extends JpaRepository<Publisher,Long> {

}
