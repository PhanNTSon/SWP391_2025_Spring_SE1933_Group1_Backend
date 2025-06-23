package com.se1933g01.steamclonebackend.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.se1933g01.steamclonebackend.entity.user.Publisher;

public interface PublisherRepo extends JpaRepository<Publisher,Long> {

}
