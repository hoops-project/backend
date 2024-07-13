package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "유저 pk", example = "4")
    private Long userId;
    @Schema(description = "게임 엔티티 pk", example = "12")
    private Long gameId;
    @Schema(description = "경기 주소", example = "서울특별시 강남구 강남대로 328 농구경기장")
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
