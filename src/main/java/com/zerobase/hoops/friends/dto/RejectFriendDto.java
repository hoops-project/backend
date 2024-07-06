package com.zerobase.hoops.friends.dto;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.type.FriendStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class RejectFriendDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @Schema(
        description = "친구 pk",
        defaultValue = "1",
        requiredMode = RequiredMode.REQUIRED)
    @NotNull(message = "친구 아이디는 필수 값입니다.")
    @Min(1)
    private Long friendId;

    public FriendEntity toEntity(UserEntity applyUserEntity,
        UserEntity friendUserEntity) {
      return FriendEntity.builder()
          .status(FriendStatus.APPLY)
          .user(applyUserEntity)
          .friendUser(friendUserEntity)
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    @Schema(description = "메세지", example = "파브리 의 친구 신청을 거절 했습니다.")
    String message;

    public RejectFriendDto.Response toDto(String message) {
      return RejectFriendDto.Response.builder()
          .message(message)
          .build();
    }
  }

}
