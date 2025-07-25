package com.se1933g01.steamclonebackend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Tag;
import com.se1933g01.steamclonebackend.entity.user.Publisher;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>, JpaSpecificationExecutor<Game> {
    List<Game> findAll();

    @Query("SELECT g FROM Game g WHERE g.state = true")
    Page<Game> findAllByStateTrue(Specification<Game> spec, Pageable pageable);

    Game findById(long id);

    @Query("SELECT g FROM Game g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Game> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<Game> findByPublisher(Publisher publisher, Pageable pageable);

    Page<Game> findByPublisherAndNameContainingIgnoreCase(Publisher publisher, String name, Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.price <= 5 AND g.price > 0 ORDER BY g.totalPurchased DESC")
    List<Game> findTop10PriceUnder5(Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.releaseDate >= :fromDate ORDER BY g.releaseDate DESC")
    List<Game> findTop10NewPublish(@Param("fromDate") LocalDate fromDate, Pageable pageable);

    @Query("SELECT g FROM Game g ORDER BY g.totalPurchased DESC")
    List<Game> findTop10TopSelling(Pageable pageable);

    @Query("""
    SELECT g
    FROM Game g
    JOIN g.reviews r
    WHERE r.isRecommended = true
    GROUP BY g
    ORDER BY COUNT(r) DESC
    """)
    List<Game> findTop16MostRecommended(Pageable pageable);

    
@Query(value = """
    SELECT gt2."GameID"
    FROM "GameTags" gt1
    JOIN "GameTags" gt2 ON gt1."TagID" = gt2."TagID"
    WHERE gt1."GameID" = :gameId AND gt2."GameID" != :gameId
    GROUP BY gt2."GameID"
    ORDER BY COUNT(*) DESC
    LIMIT 10
    """, nativeQuery = true)
List<Long> findRelatedGameIdsByTag(@Param("gameId") Long gameId);
List<Game> findDistinctByTagsInAndGameIdNotInAndStateTrue(Set<Tag> tags, Set<Long> excludeIds);
}
