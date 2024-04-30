package com.zerobase.hoops.gameCreator.repository;

import com.zerobase.hoops.entity.GameEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends
    JpaRepository<GameEntity, Long> {

  Optional<GameEntity> findByGameIdAndDeletedDateNull(Long gameId);

}


