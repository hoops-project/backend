package com.zerobase.hoops.gameCreator.repository;

import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.users.type.GenderType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantGameRepository extends
    JpaRepository<ParticipantGameEntity, Long> {

  long countByStatusAndGameId(
      ParticipantGameStatus participantGameStatus, Long gameId);

  List<ParticipantGameEntity> findByStatusInAndGameId(
      List<ParticipantGameStatus> accept, Long gameId);

  boolean existsByStatusAndGameIdAndUserGender(
      ParticipantGameStatus participantGameStatus, Long gameId,
      GenderType queryGender);

  List<ParticipantGameEntity> findByStatusAndGameId(
      ParticipantGameStatus participantGameStatus, Long gameId);

  Optional<ParticipantGameEntity> findByIdAndStatus(
      Long participantId, ParticipantGameStatus participantGameStatus);

  boolean existsByStatusInAndGameIdAndUserId(
      List<ParticipantGameStatus> participantGameStatus,
      Long gameId, Long receiverUserId);

  List<ParticipantGameEntity> findByGameIdAndStatusNotAndDeletedDateTimeNull(
      Long gameId, ParticipantGameStatus participantGameStatus);

  List<ParticipantGameEntity> findByUserIdAndStatusInAndWithdrewDateTimeNull(
      Long userId, List<ParticipantGameStatus> participantGameStatus);

  List<ParticipantGameEntity> findByGameIdAndStatusAndDeletedDateTimeNull(
      Long gameId, ParticipantGameStatus participantGameStatus);

  Optional<ParticipantGameEntity> findByStatusAndGameIdAndUserId(
      ParticipantGameStatus participantGameStatus, Long gameId, Long userId);

  boolean existsByStatusAndGameIdAndUserId(
      ParticipantGameStatus participantGameStatus, Long gameId,
      Long userId);

  boolean existsByGame_IdAndUser_IdAndStatus(
      Long gameId, Long userId, ParticipantGameStatus status);

}


