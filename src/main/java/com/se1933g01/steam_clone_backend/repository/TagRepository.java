package com.se1933g01.steam_clone_backend.repository;
import com.se1933g01.steam_clone_backend.entity.game.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

}
