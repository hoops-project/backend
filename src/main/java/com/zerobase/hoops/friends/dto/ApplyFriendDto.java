package com.zerobase.hoops.friends.dto;

import static com.zerobase.hoops.util.Common.getNowDateTime;

import com.zerobase.hoops.document.FriendDocument;
import com.zerobase.hoops.document.UserDocument;
import com.zerobase.hoops.friends.type.FriendStatus;
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

public class ApplyFriendDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    @Schema(
        description = "친구 유저 pk",
        defaultValue = "3",
        requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "친구 유저 아이디는 필수 값입니다.")
    private String friendUserId;

    public FriendDocument toDocument(UserDocument applyUser,
        UserDocument friendUser,
        long friendId,
        Clock clock) {
      return FriendDocument.builder()
          .id(Long.toString(friendId))
          .status(FriendStatus.APPLY)
          .createdDateTime(getNowDateTime(clock))
          .user(applyUser)
          .friendUser(friendUser)
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    @Schema(description = "메세지", example = "파브리 에게 친구 신청을 했습니다.")
    String message;

    public ApplyFriendDto.Response toDto(String message) {
      return ApplyFriendDto.Response.builder()
          .message(message)
          .build();
    }
  }

}
