package com.zerobase.hoops.friends.dto;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.type.FriendStatus;
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
          .friendId(friendEntity.getFriendId())
          .status(FriendStatus.REJECT)
          .createdDateTime(friendEntity.getCreatedDateTime())
          .rejectedDateTime(LocalDateTime.now())
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
  public static class RejectResponse {

    private Long friendId;

    private FriendStatus status;

    private LocalDateTime createdDateTime;

    private LocalDateTime rejectedDateTime;

    private String nickName;

    private String friendNickName;

    public static RejectResponse toDto(FriendEntity friendEntity) {
      return RejectResponse.builder()
          .friendId(friendEntity.getFriendId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .rejectedDateTime(friendEntity.getRejectedDateTime())
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
  public static class DeleteRequest {

    @NotNull(message = "친구 아이디는 필수 값입니다.")
    @Min(1)
    private Long friendId;

    public static FriendEntity toSelfEntity(FriendEntity friendEntity) {
      return FriendEntity.builder()
          .friendId(friendEntity.getFriendId())
          .status(FriendStatus.DELETE)
          .createdDateTime(friendEntity.getCreatedDateTime())
          .acceptedDateTime(friendEntity.getAcceptedDateTime())
          .deletedDateTime(LocalDateTime.now())
          .userEntity(friendEntity.getUserEntity())
          .friendUserEntity(friendEntity.getFriendUserEntity())
          .build();
    }

    public static FriendEntity toOtherEntity(FriendEntity selfFriendEntity,
        FriendEntity otherFriendEntity) {
      return FriendEntity.builder()
          .friendId(otherFriendEntity.getFriendId())
          .status(FriendStatus.DELETE)
          .createdDateTime(otherFriendEntity.getCreatedDateTime())
          .acceptedDateTime(otherFriendEntity.getAcceptedDateTime())
          .deletedDateTime(selfFriendEntity.getDeletedDateTime())
          .userEntity(otherFriendEntity.getUserEntity())
          .friendUserEntity(otherFriendEntity.getFriendUserEntity())
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
          .friendId(friendEntity.getFriendId())
          .status(friendEntity.getStatus())
          .createdDateTime(friendEntity.getCreatedDateTime())
          .acceptedDateTime(friendEntity.getAcceptedDateTime())
          .deletedDateTime(friendEntity.getDeletedDateTime())
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
          .userId(friendEntity.getFriendUserEntity().getUserId())
          .birthday(friendEntity.getFriendUserEntity().getBirthday())
          .gender(friendEntity.getFriendUserEntity().getGender())
          .nickName(friendEntity.getFriendUserEntity().getNickName())
          .playStyle(friendEntity.getFriendUserEntity().getPlayStyle())
          .ability(friendEntity.getFriendUserEntity().getAbility())
          .mannerPoint(friendEntity.getFriendUserEntity().getStringAverageRating())
          .friendId(friendEntity.getFriendId())
          .build();
    }

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
          .userId(friendEntity.getUserEntity().getUserId())
          .birthday(friendEntity.getUserEntity().getBirthday())
          .gender(friendEntity.getUserEntity().getGender())
          .nickName(friendEntity.getUserEntity().getNickName())
          .playStyle(friendEntity.getUserEntity().getPlayStyle())
          .ability(friendEntity.getUserEntity().getAbility())
          .mannerPoint(friendEntity.getUserEntity().getStringAverageRating())
          .friendId(friendEntity.getFriendId())
          .build();
    }

  }

}
