package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipateGameDto {

  private Long participantId;
  private ParticipantGameStatus status;
  private LocalDateTime createdDateTime;
  private LocalDateTime acceptedDateTime;
  private LocalDateTime rejectedDateTime;
  private LocalDateTime canceledDateTime;
  private LocalDateTime withdrewDateTime;
  private LocalDateTime kickoutDateTime;
  private LocalDateTime deletedDateTime;

  private GameEntity gameEntity;
  private UserEntity userEntity;

  public static ParticipateGameDto fromEntity(
      ParticipantGameEntity participantGameEntity) {

    return ParticipateGameDto.builder()
        .participantId(participantGameEntity.getParticipantId())
        .status(participantGameEntity.getStatus())
        .createdDateTime(participantGameEntity.getCreatedDateTime())
        .acceptedDateTime(participantGameEntity.getAcceptedDateTime())
        .rejectedDateTime(participantGameEntity.getRejectedDateTime())
        .canceledDateTime(participantGameEntity.getCanceledDateTime())
        .withdrewDateTime(participantGameEntity.getWithdrewDateTime())
        .kickoutDateTime(participantGameEntity.getKickoutDateTime())
        .deletedDateTime(participantGameEntity.getDeletedDateTime())
        .gameEntity(participantGameEntity.getGameEntity())
        .userEntity(participantGameEntity.getUserEntity())
        .build();
  }

}
