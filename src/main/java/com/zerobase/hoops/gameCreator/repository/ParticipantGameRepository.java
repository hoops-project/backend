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

  long countByStatusAndGameEntityGameId(
      ParticipantGameStatus participantGameStatus, Long gameId);

  List<ParticipantGameEntity> findByStatusInAndGameEntityGameId(
      List<ParticipantGameStatus> accept, Long gameId);

  boolean existsByStatusAndGameEntityGameIdAndUserEntityGender(
      ParticipantGameStatus participantGameStatus, Long gameId,
      GenderType queryGender);

  List<ParticipantGameEntity> findByStatusAndGameEntityGameId(
      ParticipantGameStatus participantGameStatus, Long gameId);

  Optional<ParticipantGameEntity> findByParticipantIdAndStatus(
      Long participantId, ParticipantGameStatus participantGameStatus);

  boolean existsByStatusInAndGameEntityGameIdAndUserEntityUserId(
      List<ParticipantGameStatus> participantGameStatus,
      Long gameId, Long receiverUserId);

  List<ParticipantGameEntity> findByGameEntityGameIdAndStatusNotAndDeletedDateTimeNull(
      Long gameId, ParticipantGameStatus participantGameStatus);

  List<ParticipantGameEntity> findByUserEntityUserIdAndStatusInAndWithdrewDateTimeNull(
      Long userId, List<ParticipantGameStatus> participantGameStatus);

  List<ParticipantGameEntity> findByGameEntityGameIdAndStatusAndDeletedDateTimeNull(
      Long gameId, ParticipantGameStatus participantGameStatus);

  Optional<ParticipantGameEntity> findByStatusAndGameEntityGameIdAndUserEntityUserId(
      ParticipantGameStatus participantGameStatus, Long gameId, Long userId);

  boolean existsByStatusAndGameEntityGameIdAndUserEntityUserId(
      ParticipantGameStatus participantGameStatus, Long gameId,
      Long userId);

  boolean existsByGameEntity_GameIdAndUserEntity_UserIdAndStatus(
      Long gameId, Long userId, ParticipantGameStatus status);

}


