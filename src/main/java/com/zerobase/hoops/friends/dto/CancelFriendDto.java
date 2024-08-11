package com.zerobase.hoops.friends.dto;

import com.zerobase.hoops.document.FriendDocument;
import com.zerobase.hoops.document.UserDocument;
import com.zerobase.hoops.friends.type.FriendStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class CancelFriendDto {

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
    @NotBlank(message = "친구 아이디는 필수 값입니다.")
    private String friendId;

    public FriendDocument toDocument(UserDocument applyUser,
        UserDocument friendUser) {
      return FriendDocument.builder()
          .status(FriendStatus.APPLY)
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

    @Schema(description = "메세지", example = "파브리 에게 친구 신청 한것을 취소 했습니다.")
    String message;

    public CancelFriendDto.Response toDto(String message) {
      return CancelFriendDto.Response.builder()
          .message(message)
          .build();
    }
  }

}
