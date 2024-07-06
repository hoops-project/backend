package com.zerobase.hoops.friends.dto;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "친구 리스트 조회 유저 pk", example = "3")
    private Long userId;

    @Schema(description = "친구 리스트 조회 유저 생년월일", example = "1999-01-01")
    private LocalDate birthday;

    @Schema(description = "친구 리스트 조회 유저 성별", example = "MALE")
    private GenderType gender;

    @Schema(description = "친구 리스트 유저 닉네임", example = "파브리")
    private String nickName;

    @Schema(description = "친구 리스트 유저 플레이스타일", example = "BALANCE")
    private PlayStyleType playStyle;

    @Schema(description = "친구 리스트 유저 능력", example = "SPEED")
    private AbilityType ability;

    @Schema(description = "친구 리스트 유저 매너점수", example = "3.5")
    private String mannerPoint;

    @Schema(description = "친구 pk", example = "1")
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
