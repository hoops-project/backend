package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.document.ParticipantGameDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MannerPointListResponse {

  @Schema(description = "게임엔티티 pk", example = "10")
  private String gameId;
  @Schema(description = "게임 방제", example = "오늘 농구 한 판 하실분!")
  private String title;
  @Schema(description = "경기장 주소", example = "서울특별시 강남구 강남대로 328 농구경기장")
  private String address;
  @Schema(description = "유저 닉네임", example = "스테판카레")
  private String player;
  @Schema(description = "유저 pk", example = "3")
  private String playerId;

  public static MannerPointListResponse of(
      ParticipantGameDocument participantGame) {
    return MannerPointListResponse.builder()
        .gameId(participantGame.getGame().getId())
        .title(participantGame.getGame().getTitle())
        .address(participantGame.getGame().getAddress())
        .player(participantGame.getUser().getNickName())
        .playerId(participantGame.getUser().getId())
        .build();
  }
}
