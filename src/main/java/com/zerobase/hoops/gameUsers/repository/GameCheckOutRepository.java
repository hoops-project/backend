package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameCheckOutRepository extends
    JpaRepository<ParticipantGameEntity, Long> {

  int countByStatusAndGameEntityGameId(
      ParticipantGameStatus participantGameStatus, Long id);

  Optional<List<ParticipantGameEntity>> findByUserEntity_UserIdAndStatus(Long userId,
      ParticipantGameStatus status);

  boolean existsByGameEntity_GameIdAndUserEntity_UserId(Long gameId,
      Long userId);

  Optional<List<ParticipantGameEntity>> findByStatusAndGameEntity_GameId(
      ParticipantGameStatus status, Long gameId);

  boolean existsByGameEntity_GameIdAndUserEntity_UserIdAndStatus(
      Long gameId, Long userId, ParticipantGameStatus status);


}
