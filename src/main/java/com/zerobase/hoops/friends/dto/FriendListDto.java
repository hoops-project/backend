package com.zerobase.hoops.friends.dto;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class FriendListDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    private Long userId;

    private LocalDate birthday;

    private GenderType gender;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    private Long friendId;

    public static Response toDto(FriendEntity friendEntity) {
      return Response.builder()
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

    public static Response toRequestFriendListDto(FriendEntity friendEntity) {
      return Response.builder()
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
      Response that = (Response) o;
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
