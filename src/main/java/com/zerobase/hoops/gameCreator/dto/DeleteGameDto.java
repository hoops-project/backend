package com.zerobase.hoops.gameCreator.dto;

import static com.zerobase.hoops.util.Common.getNowDateTime;

import com.zerobase.hoops.document.GameDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class DeleteGameDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @Schema(
        description = "경기 pk",
        defaultValue = "1",
        requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "게임 아이디는 필수 입력 값 입니다.")
    private String gameId;

    public GameDocument toDocument(GameDocument game, Clock clock) {
      return GameDocument.builder()
          .id(game.getId())
          .title(game.getTitle())
          .content(game.getContent())
          .headCount(game.getHeadCount())
          .fieldStatus(game.getFieldStatus())
          .gender(game.getGender())
          .startDateTime(game.getStartDateTime())
          .createdDateTime(game.getCreatedDateTime())
          .deletedDateTime(getNowDateTime(clock))
          .inviteYn(game.getInviteYn())
          .address(game.getAddress())
          .placeName(game.getPlaceName())
          .latitude(game.getLatitude())
          .longitude(game.getLongitude())
          .cityName(game.getCityName())
          .matchFormat(game.getMatchFormat())
          .user(game.getUser())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    String message;

    public DeleteGameDto.Response toDto(String message) {
      return DeleteGameDto.Response.builder()
          .message(message)
          .build();
    }
  }

}
