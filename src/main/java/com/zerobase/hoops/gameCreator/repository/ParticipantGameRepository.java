package com.zerobase.hoops.gameCreator.repository;

import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.users.type.GenderType;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantGameRepository extends
    JpaRepository<ParticipantGameEntity, Long> {

  OptionalLong countByStatusAndGameEntityGameId(ParticipantGameStatus participantGameStatus,
      Long gameId);

  List<ParticipantGameEntity> findByStatusInAndGameEntityGameId(
      List<ParticipantGameStatus> accept, Long gameId);

  OptionalLong countByStatusAndGameEntityGameIdAndUserEntityGender(ParticipantGameStatus participantGameStatus, Long gameId, GenderType queryGender);
}


