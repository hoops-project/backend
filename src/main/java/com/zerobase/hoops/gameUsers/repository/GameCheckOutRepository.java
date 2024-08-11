package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.document.ParticipantGameDocument;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameCheckOutRepository extends
    ElasticsearchRepository<ParticipantGameDocument, String> {

  int countByStatusAndGameId(
      ParticipantGameStatus participantGameStatus, String id);

  Optional<List<ParticipantGameDocument>> findByUser_IdAndStatus(String userId,
      ParticipantGameStatus status);

  boolean existsByGame_IdAndUser_Id(String gameId, String userId);

  Optional<List<ParticipantGameDocument>> findByStatusAndGame_Id(
      ParticipantGameStatus status, String gameId);

  boolean existsByGame_IdAndUser_IdAndStatus(
      String gameId, String userId, ParticipantGameStatus status);


}
