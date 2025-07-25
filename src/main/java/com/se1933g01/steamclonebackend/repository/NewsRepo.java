package com.se1933g01.steamclonebackend.repository;

import com.se1933g01.steamclonebackend.entity.news.News;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepo extends JpaRepository<News, Long> {
    Page<News> findByGame_GameId(Long gameId, Pageable pageable);

}
