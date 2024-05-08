package com.zerobase.hoops.friends.dto;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.service.FriendService;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.gameCreator.dto.GameDto;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateRequest;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class FriendDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ApplyRequest {

    @NotNull(message = "친구 유저 아이디는 필수 값입니다.")
    @Min(1)
    private Long friendUserId;

    public static FriendEntity toEntity(UserEntity applyUserEntity,
        UserEntity friendUserEntity) {
      return FriendEntity.builder()
          .status(FriendStatus.APPLY)
          .applyUserEntity(applyUserEntity)
          .friendUserEntity(friendUserEntity)
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ApplyResponse {

    private Long friendId;

    private FriendStatus status;

    private LocalDateTime createdDateTime;

    private String applyNickName;

    private String friendNickName;

    public static ApplyResponse toDto(FriendEntity friendEntity) {
      return ApplyResponse.builder()
          .friendId(friendEntity.getFriendId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .applyNickName(friendEntity.getApplyUserEntity().getNickName())
          .friendNickName(friendEntity.getFriendUserEntity().getNickName())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CancelRequest {

    @NotNull(message = "친구 아이디는 필수 값입니다.")
    @Min(1)
    private Long friendId;

    public static FriendEntity toEntity(FriendEntity friendEntity) {
      return FriendEntity.builder()
          .friendId(friendEntity.getFriendId())
          .status(FriendStatus.CANCEL)
          .createdDateTime(friendEntity.getCreatedDateTime())
          .canceledDateTime(friendEntity.getCanceledDateTime())
          .applyUserEntity(friendEntity.getApplyUserEntity())
          .friendUserEntity(friendEntity.getFriendUserEntity())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CancelResponse {

    private Long friendId;

    private FriendStatus status;

    private LocalDateTime createdDateTime;

    private LocalDateTime canceledDateTime;

    private String applyNickName;

    private String friendNickName;

    public static CancelResponse toDto(FriendEntity friendEntity) {
      return CancelResponse.builder()
          .friendId(friendEntity.getFriendId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .canceledDateTime(friendEntity.getCanceledDateTime())
          .applyNickName(friendEntity.getApplyUserEntity().getNickName())
          .friendNickName(friendEntity.getFriendUserEntity().getNickName())
          .build();
    }
  }

}
