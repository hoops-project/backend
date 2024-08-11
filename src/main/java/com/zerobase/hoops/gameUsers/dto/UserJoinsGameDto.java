package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

    @NotBlank
    private String gameId;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    @Schema(description = "유저 pk", example = "4")
    private String userId;
    @Schema(description = "게임 엔티티 pk", example = "12")
    private String gameId;
    @Schema(description = "경기 주소", example = "서울특별시 강남구 강남대로 328 농구경기장")
    private String gameAddress;
    private ParticipantGameStatus participantGameStatus;
    private OffsetDateTime createdDateTime;

    public static Response from(ParticipateGameDto participateGameDto) {
      return Response.builder()
          .userId(participateGameDto.getUserDocument().getId())
          .gameId(participateGameDto.getUserDocument().getId())
          .gameAddress(participateGameDto.getGameDocument().getAddress())
          .participantGameStatus(ParticipantGameStatus.APPLY)
          .createdDateTime(participateGameDto.getCreatedDateTime())
          .build();
    }
  }
}
