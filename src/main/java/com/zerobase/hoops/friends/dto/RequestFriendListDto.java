package com.zerobase.hoops.friends.dto;

import com.zerobase.hoops.document.FriendDocument;
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

public class RequestFriendListDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    @Schema(description = "내가 친구 요청 받은 리스트 유저 pk", example = "2")
    private String userId;

    @Schema(description = "내가 친구 요청 받은 리스트 유저 생년월일", example = "1995-01-01")
    private LocalDate birthday;

    @Schema(description = "내가 친구 요청 받은 리스트 유저 성별", example = "MALE")
    private GenderType gender;

    @Schema(description = "내가 친구 요청 받은 리스트 유저 닉네임", example = "구름")
    private String nickName;

    @Schema(description = "내가 친구 요청 받은 리스트 유저 플레이스타일", example = "BALANCE")
    private PlayStyleType playStyle;

    @Schema(description = "내가 친구 요청 받은 리스트 유저 능력", example = "SPEED")
    private AbilityType ability;

    @Schema(description = "내가 친구 요청 받은 리스트 유저 매너점수", example = "3.5")
    private String mannerPoint;

    @Schema(description = "친구 pk", example = "1")
    private String friendId;

    public static Response toDto(FriendDocument friendDocument) {
      return Response.builder()
          .userId(friendDocument.getUser().getId())
          .birthday(friendDocument.getUser().getBirthday())
          .gender(friendDocument.getUser().getGender())
          .nickName(friendDocument.getUser().getNickName())
          .playStyle(friendDocument.getUser().getPlayStyle())
          .ability(friendDocument.getUser().getAbility())
          .mannerPoint(friendDocument.getUser().getStringAverageRating())
          .friendId(friendDocument.getId())
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
