package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserJoinsGameDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

    @NotNull
    @Min(1)
    private Long gameId;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    private Long userId;
    private Long gameId;
    private String gameAddress;
    private ParticipantGameStatus participantGameStatus;
    private LocalDateTime createdDateTime;

    public static Response from(ParticipateGameDto participateGameDto) {
      return Response.builder()
          .userId(participateGameDto.getUserEntity().getId())
          .gameId(participateGameDto.getGameEntity().getId())
          .gameAddress(participateGameDto.getGameEntity().getAddress())
          .participantGameStatus(ParticipantGameStatus.APPLY)
          .createdDateTime(participateGameDto.getCreatedDateTime())
          .build();
    }
  }
}
