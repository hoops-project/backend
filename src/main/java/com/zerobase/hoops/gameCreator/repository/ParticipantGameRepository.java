package com.zerobase.hoops.gameCreator.repository;

import com.zerobase.hoops.document.ParticipantGameDocument;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.users.type.GenderType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantGameRepository extends
    ElasticsearchRepository<ParticipantGameDocument, String> {

  int countByStatusAndGameId(
      ParticipantGameStatus participantGameStatus, String gameId);

  List<ParticipantGameDocument> findByStatusInAndGameId(
      List<ParticipantGameStatus> accept, String gameId);

  boolean existsByStatusAndGameIdAndUserGender(
      ParticipantGameStatus participantGameStatus, String gameId,
      GenderType queryGender);

  Page<ParticipantGameDocument> findByStatusAndGameId(
      ParticipantGameStatus participantGameStatus, String gameId, Pageable pageable);

  Optional<ParticipantGameDocument> findByIdAndStatus(
      String participantId, ParticipantGameStatus participantGameStatus);

  boolean existsByStatusInAndGameIdAndUserId(
      List<ParticipantGameStatus> participantGameStatus,
      String gameId, String receiverUserId);

  List<ParticipantGameDocument> findByGameIdAndStatusNotAndDeletedDateTimeNull(
      String gameId, ParticipantGameStatus participantGameStatus);

  List<ParticipantGameDocument> findByUserIdAndStatusInAndWithdrewDateTimeNull(
      String userId, List<ParticipantGameStatus> participantGameStatus);

  List<ParticipantGameDocument> findByGameIdAndStatusAndDeletedDateTimeNull(
      String gameId, ParticipantGameStatus participantGameStatus);

  Optional<ParticipantGameDocument> findByStatusAndGameIdAndUserId(
      ParticipantGameStatus participantGameStatus, String gameId, String userId);

  boolean existsByStatusAndGameIdAndUserId(
      ParticipantGameStatus participantGameStatus, String gameId,
      String userId);

  boolean existsByGame_IdAndUser_IdAndStatus(
      String gameId, String userId, ParticipantGameStatus status);

}


