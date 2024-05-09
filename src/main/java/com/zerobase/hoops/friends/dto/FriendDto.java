package com.zerobase.hoops.friends.dto;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.type.FriendStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
          .userEntity(applyUserEntity)
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

    private String nickName;

    private String friendNickName;

    public static ApplyResponse toDto(FriendEntity friendEntity) {
      return ApplyResponse.builder()
          .friendId(friendEntity.getFriendId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .nickName(friendEntity.getUserEntity().getNickName())
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
          .canceledDateTime(LocalDateTime.now())
          .userEntity(friendEntity.getUserEntity())
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

    private String nickName;

    private String friendNickName;

    public static CancelResponse toDto(FriendEntity friendEntity) {
      return CancelResponse.builder()
          .friendId(friendEntity.getFriendId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .canceledDateTime(friendEntity.getCanceledDateTime())
          .nickName(friendEntity.getUserEntity().getNickName())
          .friendNickName(friendEntity.getFriendUserEntity().getNickName())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AcceptRequest {

    @NotNull(message = "친구 아이디는 필수 값입니다.")
    @Min(1)
    private Long friendId;

    public static FriendEntity toSelfEntity(FriendEntity friendEntity) {
      return FriendEntity.builder()
          .friendId(friendEntity.getFriendId())
          .status(FriendStatus.ACCEPT)
          .createdDateTime(friendEntity.getCreatedDateTime())
          .acceptedDateTime(LocalDateTime.now())
          .userEntity(friendEntity.getUserEntity())
          .friendUserEntity(friendEntity.getFriendUserEntity())
          .build();
    }

    public static FriendEntity toOtherEntity(FriendEntity friendEntity) {
      return FriendEntity.builder()
          .friendId(friendEntity.getFriendId())
          .status(FriendStatus.ACCEPT)
          .createdDateTime(friendEntity.getCreatedDateTime())
          .acceptedDateTime(friendEntity.getAcceptedDateTime())
          .userEntity(friendEntity.getFriendUserEntity())
          .friendUserEntity(friendEntity.getUserEntity())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AcceptResponse {

    private Long friendId;

    private FriendStatus status;

    private LocalDateTime createdDateTime;

    private LocalDateTime acceptedDateTime;

    private String nickName;

    private String friendNickName;

    public static AcceptResponse toDto(FriendEntity friendEntity) {
      return AcceptResponse.builder()
          .friendId(friendEntity.getFriendId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .acceptedDateTime(friendEntity.getAcceptedDateTime())
          .nickName(friendEntity.getUserEntity().getNickName())
          .friendNickName(friendEntity.getFriendUserEntity().getNickName())
          .build();
    }
  }

}
