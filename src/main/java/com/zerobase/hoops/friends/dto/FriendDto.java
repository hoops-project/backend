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
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
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
  public static class CommonRequest {

    @NotNull(message = "친구 아이디는 필수 값입니다.")
    @Min(1)
    private Long friendId;

  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class FriendListResponse {

    private Long userId;

    private LocalDate birthday;

    private GenderType gender;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    private Long friendId;

    public static FriendListResponse toDto(FriendEntity friendEntity) {
      return FriendListResponse.builder()
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

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      FriendListResponse that = (FriendListResponse) o;
      return Objects.equals(userId, that.userId) &&
          Objects.equals(birthday, that.birthday) &&
          Objects.equals(gender, that.gender) &&
          Objects.equals(nickName, that.nickName) &&
          Objects.equals(playStyle, that.playStyle) &&
          Objects.equals(ability, that.ability) &&
          Objects.equals(mannerPoint, that.mannerPoint) &&
          Objects.equals(friendId, that.friendId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(userId, birthday, gender, nickName, playStyle,
          ability, mannerPoint, friendId);
    }

  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class InviteFriendListResponse {

    private Long userId;

    private LocalDate birthday;

    private GenderType gender;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    private InviteStatus status;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      InviteFriendListResponse that = (InviteFriendListResponse) o;
      return Objects.equals(userId, that.userId) &&
          Objects.equals(birthday, that.birthday) &&
          Objects.equals(gender, that.gender) &&
          Objects.equals(nickName, that.nickName) &&
          Objects.equals(playStyle, that.playStyle) &&
          Objects.equals(ability, that.ability) &&
          Objects.equals(mannerPoint, that.mannerPoint) &&
          Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
      return Objects.hash(userId, birthday, gender, nickName, playStyle,
          ability, mannerPoint, status);
    }

  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RequestFriendListResponse {

    private Long userId;

    private LocalDate birthday;

    private GenderType gender;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    private Long friendId;

    public static RequestFriendListResponse toDto(FriendEntity friendEntity) {
      return RequestFriendListResponse.builder()
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

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      RequestFriendListResponse that = (RequestFriendListResponse) o;
      return Objects.equals(userId, that.userId) &&
          Objects.equals(birthday, that.birthday) &&
          Objects.equals(gender, that.gender) &&
          Objects.equals(nickName, that.nickName) &&
          Objects.equals(playStyle, that.playStyle) &&
          Objects.equals(ability, that.ability) &&
          Objects.equals(mannerPoint, that.mannerPoint) &&
          Objects.equals(friendId, that.friendId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(userId, birthday, gender, nickName, playStyle,
          ability, mannerPoint, friendId);
    }

  }

}
