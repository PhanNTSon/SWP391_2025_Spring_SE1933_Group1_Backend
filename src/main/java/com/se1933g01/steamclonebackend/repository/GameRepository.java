package com.se1933g01.steamclonebackend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.user.Publisher;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>, JpaSpecificationExecutor<Game> {
    List<Game> findAll();

    Game findById(long id);

    @Query("SELECT g FROM Game g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Game> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<Game> findByPublisher(Publisher publisher, Pageable pageable);

    Page<Game> findByPublisherAndNameContainingIgnoreCase(Publisher publisher, String name, Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.price <= 5 ORDER BY g.totalPurchased DESC")
    List<Game> findTop10PriceUnder5(Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.releaseDate >= :fromDate ORDER BY g.releaseDate DESC")
    List<Game> findTop10NewPublish(@Param("fromDate") LocalDate fromDate, Pageable pageable);

    @Query("SELECT g FROM Game g ORDER BY g.totalPurchased DESC")
    List<Game> findTop10TopSelling(Pageable pageable);
}
