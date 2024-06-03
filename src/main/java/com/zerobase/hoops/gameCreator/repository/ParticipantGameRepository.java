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

  long countByStatusAndGameEntityId(
      ParticipantGameStatus participantGameStatus, Long gameId);

  List<ParticipantGameEntity> findByStatusInAndGameEntityId(
      List<ParticipantGameStatus> accept, Long gameId);

  boolean existsByStatusAndGameEntityIdAndUserEntityGender(
      ParticipantGameStatus participantGameStatus, Long gameId,
      GenderType queryGender);

  List<ParticipantGameEntity> findByStatusAndGameEntityId(
      ParticipantGameStatus participantGameStatus, Long gameId);

  Optional<ParticipantGameEntity> findByIdAndStatus(
      Long participantId, ParticipantGameStatus participantGameStatus);

  boolean existsByStatusInAndGameEntityIdAndUserEntityId(
      List<ParticipantGameStatus> participantGameStatus,
      Long gameId, Long receiverUserId);

  List<ParticipantGameEntity> findByGameEntityIdAndStatusNotAndDeletedDateTimeNull(
      Long gameId, ParticipantGameStatus participantGameStatus);

  List<ParticipantGameEntity> findByUserEntityIdAndStatusInAndWithdrewDateTimeNull(
      Long userId, List<ParticipantGameStatus> participantGameStatus);

  List<ParticipantGameEntity> findByGameEntityIdAndStatusAndDeletedDateTimeNull(
      Long gameId, ParticipantGameStatus participantGameStatus);

  Optional<ParticipantGameEntity> findByStatusAndGameEntityIdAndUserEntityId(
      ParticipantGameStatus participantGameStatus, Long gameId, Long userId);

  boolean existsByStatusAndGameEntityIdAndUserEntityId(
      ParticipantGameStatus participantGameStatus, Long gameId,
      Long userId);

  boolean existsByGameEntity_IdAndUserEntity_IdAndStatus(
      Long gameId, Long userId, ParticipantGameStatus status);

}


