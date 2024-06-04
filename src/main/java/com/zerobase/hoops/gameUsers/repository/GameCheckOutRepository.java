package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameCheckOutRepository extends
    JpaRepository<ParticipantGameEntity, Long> {

  int countByStatusAndGameId(
      ParticipantGameStatus participantGameStatus, Long id);

  Optional<List<ParticipantGameEntity>> findByUser_IdAndStatus(Long userId,
      ParticipantGameStatus status);

  boolean existsByGame_IdAndUser_Id(Long gameId,
      Long userId);

  Optional<List<ParticipantGameEntity>> findByStatusAndGame_Id(
      ParticipantGameStatus status, Long gameId);

  boolean existsByGame_IdAndUser_IdAndStatus(
      Long gameId, Long userId, ParticipantGameStatus status);


}
