package com.zerobase.hoops.friends.dto;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
  public static class ApplyResponse {

    private Long friendId;

    private FriendStatus status;

    private LocalDateTime createdDateTime;

    private String nickName;

    private String friendNickName;

    public static ApplyResponse toDto(FriendEntity friendEntity) {
      return ApplyResponse.builder()
          .friendId(friendEntity.getId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .nickName(friendEntity.getUser().getNickName())
          .friendNickName(friendEntity.getFriendUser().getNickName())
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
          .id(friendEntity.getId())
          .status(FriendStatus.CANCEL)
          .createdDateTime(friendEntity.getCreatedDateTime())
          .canceledDateTime(LocalDateTime.now())
          .user(friendEntity.getUser())
          .friendUser(friendEntity.getFriendUser())
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
          .friendId(friendEntity.getId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .canceledDateTime(friendEntity.getCanceledDateTime())
          .nickName(friendEntity.getUser().getNickName())
          .friendNickName(friendEntity.getFriendUser().getNickName())
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
          .id(friendEntity.getId())
          .status(FriendStatus.ACCEPT)
          .createdDateTime(friendEntity.getCreatedDateTime())
          .acceptedDateTime(LocalDateTime.now())
          .user(friendEntity.getUser())
          .friendUser(friendEntity.getFriendUser())
          .build();
    }

    public static FriendEntity toOtherEntity(FriendEntity friendEntity) {
      return FriendEntity.builder()
          .status(FriendStatus.ACCEPT)
          .createdDateTime(friendEntity.getCreatedDateTime())
          .acceptedDateTime(friendEntity.getAcceptedDateTime())
          .user(friendEntity.getFriendUser())
          .friendUser(friendEntity.getUser())
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
          .friendId(friendEntity.getId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .acceptedDateTime(friendEntity.getAcceptedDateTime())
          .nickName(friendEntity.getUser().getNickName())
          .friendNickName(friendEntity.getFriendUser().getNickName())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RejectRequest {

    @NotNull(message = "친구 아이디는 필수 값입니다.")
    @Min(1)
    private Long friendId;

    public static FriendEntity toEntity(FriendEntity friendEntity) {
      return FriendEntity.builder()
          .id(friendEntity.getId())
          .status(FriendStatus.REJECT)
          .createdDateTime(friendEntity.getCreatedDateTime())
          .rejectedDateTime(LocalDateTime.now())
          .user(friendEntity.getUser())
          .friendUser(friendEntity.getFriendUser())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RejectResponse {

    private Long friendId;

    private FriendStatus status;

    private LocalDateTime createdDateTime;

    private LocalDateTime rejectedDateTime;

    private String nickName;

    private String friendNickName;

    public static RejectResponse toDto(FriendEntity friendEntity) {
      return RejectResponse.builder()
          .friendId(friendEntity.getId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .rejectedDateTime(friendEntity.getRejectedDateTime())
          .nickName(friendEntity.getUser().getNickName())
          .friendNickName(friendEntity.getFriendUser().getNickName())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DeleteRequest {

    @NotNull(message = "친구 아이디는 필수 값입니다.")
    @Min(1)
    private Long friendId;

    public static FriendEntity toSelfEntity(FriendEntity friendEntity) {
      return FriendEntity.builder()
          .id(friendEntity.getId())
          .status(FriendStatus.DELETE)
          .createdDateTime(friendEntity.getCreatedDateTime())
          .acceptedDateTime(friendEntity.getAcceptedDateTime())
          .deletedDateTime(LocalDateTime.now())
          .user(friendEntity.getUser())
          .friendUser(friendEntity.getFriendUser())
          .build();
    }

    public static FriendEntity toOtherEntity(FriendEntity selfFriendEntity,
        FriendEntity otherFriendEntity) {
      return FriendEntity.builder()
          .id(otherFriendEntity.getId())
          .status(FriendStatus.DELETE)
          .createdDateTime(otherFriendEntity.getCreatedDateTime())
          .acceptedDateTime(otherFriendEntity.getAcceptedDateTime())
          .deletedDateTime(selfFriendEntity.getDeletedDateTime())
          .user(otherFriendEntity.getUser())
          .friendUser(otherFriendEntity.getFriendUser())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DeleteResponse {

    private Long friendId;

    private FriendStatus status;

    private LocalDateTime createdDateTime;

    private LocalDateTime acceptedDateTime;

    private LocalDateTime deletedDateTime;

    private String nickName;

    private String friendNickName;

    public static DeleteResponse toDto(FriendEntity friendEntity) {
      return DeleteResponse.builder()
          .friendId(friendEntity.getId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .acceptedDateTime(friendEntity.getAcceptedDateTime())
          .deletedDateTime(friendEntity.getDeletedDateTime())
          .nickName(friendEntity.getUser().getNickName())
          .friendNickName(friendEntity.getFriendUser().getNickName())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ListResponse {

    private Long userId;

    private LocalDate birthday;

    private GenderType gender;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    private Long friendId;

    public static ListResponse toDto(FriendEntity friendEntity) {
      return ListResponse.builder()
          .userId(friendEntity.getFriendUser().getId())
          .birthday(friendEntity.getFriendUser().getBirthday())
          .gender(friendEntity.getFriendUser().getGender())
          .nickName(friendEntity.getFriendUser().getNickName())
          .playStyle(friendEntity.getFriendUser().getPlayStyle())
          .ability(friendEntity.getFriendUser().getAbility())
          .mannerPoint(friendEntity.getFriendUser().getStringAverageRating())
          .friendId(friendEntity.getId())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class InviteListResponse {

    private Long userId;

    private LocalDate birthday;

    private GenderType gender;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    private InviteStatus status;
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RequestListResponse {

    private Long userId;

    private LocalDate birthday;

    private GenderType gender;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    private Long friendId;

    public static RequestListResponse toDto(FriendEntity friendEntity) {
      return RequestListResponse.builder()
          .userId(friendEntity.getUser().getId())
          .birthday(friendEntity.getUser().getBirthday())
          .gender(friendEntity.getUser().getGender())
          .nickName(friendEntity.getUser().getNickName())
          .playStyle(friendEntity.getUser().getPlayStyle())
          .ability(friendEntity.getUser().getAbility())
          .mannerPoint(friendEntity.getUser().getStringAverageRating())
          .friendId(friendEntity.getId())
          .build();
    }

  }

}
