package com.zerobase.hoops.gameCreator.dto;

import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class ParticipantDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DetailResponse {
    private Long participantId;

    private ParticipantGameStatus status;

    private LocalDateTime createdDateTime;

    private Long gameId;

    private Long userId;

    public static ParticipantDto.DetailResponse toDto(
        ParticipantGameEntity participantGameEntity){
      return DetailResponse.builder()
          .participantId(participantGameEntity.getParticipantId())
          .status(participantGameEntity.getStatus())
          .createdDateTime(participantGameEntity.getCreatedDateTime())
          .gameId(participantGameEntity.getGameEntity().getGameId())
          .userId(participantGameEntity.getUserEntity().getUserId())
          .build();
    }

    // 테스트 코드용 List 간 equals
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DetailResponse that = (DetailResponse) o;
      return Objects.equals(participantId, that.participantId) &&
          Objects.equals(status, that.status) &&
          Objects.equals(createdDateTime, that.createdDateTime) &&
          Objects.equals(gameId, that.gameId) &&
          Objects.equals(userId, that.userId);
    }

  }

}
