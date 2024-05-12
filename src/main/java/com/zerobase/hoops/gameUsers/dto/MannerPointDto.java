package com.zerobase.hoops.gameUsers.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.MannerPointEntity;
import com.zerobase.hoops.entity.UserEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MannerPointDto {

  @NotNull
  @Min(1)
  private Long receiverId;

  @NotNull
  @Min(1)
  private Long gameId;

  @NotNull
  @Min(1)
  @Max(5)
  private int point;

  public MannerPointEntity toEntity(
      UserEntity user, UserEntity receiver, GameEntity game) {
    return MannerPointEntity.builder()
        .point(this.point)
        .user(user)
        .receiver(receiver)
        .game(game)
        .build();
  }
}
