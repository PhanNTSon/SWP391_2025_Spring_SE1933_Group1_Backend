package com.se1933g01.steamclonebackend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.game.Game;
/**
 * Author: Phan son
 */

 @Repository
public interface GameRepo extends JpaRepository<Game,Long>{
    boolean existsByName(String name);
}
