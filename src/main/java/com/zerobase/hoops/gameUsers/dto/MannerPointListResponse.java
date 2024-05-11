package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MannerPointListResponse {

  private Long gameId;
  private String title;
  private String address;
  private String player;

  public static MannerPointListResponse of(
      ParticipantGameEntity participantGame) {
    return MannerPointListResponse.builder()
        .gameId(participantGame.getGameEntity().getGameId())
        .title(participantGame.getGameEntity().getTitle())
        .address(participantGame.getGameEntity().getAddress())
        .player(participantGame.getUserEntity().getNickName())
        .build();
  }
}
