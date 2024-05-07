package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameCheckOutRepository extends
    JpaRepository<ParticipantGameEntity, Long> {

  int countByStatusAndGameEntityGameId(
      ParticipantGameStatus participantGameStatus, Long id);

}
