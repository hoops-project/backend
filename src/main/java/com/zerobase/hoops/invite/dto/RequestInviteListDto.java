package com.zerobase.hoops.invite.dto;

import com.zerobase.hoops.entity.InviteEntity;
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

public class RequestInviteListDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    @Schema(description = "내가 초대 요청 받은 리스트 경기 초대 pk", example = "1")
    private Long inviteId;

    @Schema(description = "내가 초대 요청 받은 리스트 유저 생년월일", example = "2001-01-01")
    private LocalDate birthday;

    @Schema(description = "내가 초대 요청 받은 리스트 유저 성별", example = "MALE")
    private GenderType gender;

    @Schema(description = "내가 초대 요청 받은 리스트 유저 닉네임", example = "구름")
    private String nickName;

    @Schema(description = "내가 초대 요청 받은 리스트 유저 플레이스타일", example = "AGGRESSIVE")
    private PlayStyleType playStyle;

    @Schema(description = "내가 초대 요청 받은 리스트 유저 능력", example = "PASS")
    private AbilityType ability;

    @Schema(description = "내가 초대 요청 받은 리스트 유저 매너점수", example = "3.5")
    private String mannerPoint;

    @Schema(description = "내가 초대 요청 받은 리스트 유저 pk", example = "2")
    private Long gameId;


    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Response that = (Response) o;
      return Objects.equals(inviteId, that.inviteId) &&
          Objects.equals(birthday, that.birthday) &&
          Objects.equals(gender, that.gender) &&
          Objects.equals(nickName, that.nickName) &&
          Objects.equals(playStyle, that.playStyle) &&
          Objects.equals(ability, that.ability) &&
          Objects.equals(mannerPoint, that.mannerPoint) &&
          Objects.equals(gameId, that.gameId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(inviteId, birthday, gender, nickName,
          playStyle, ability, mannerPoint, gameId);
    }

    public static Response toDto(InviteEntity inviteEntity) {
      return Response.builder()
          .inviteId(inviteEntity.getId())
          .birthday(inviteEntity.getSenderUser().getBirthday())
          .gender(inviteEntity.getSenderUser().getGender())
          .nickName(inviteEntity.getSenderUser().getNickName())
          .playStyle(inviteEntity.getSenderUser().getPlayStyle())
          .ability(inviteEntity.getSenderUser().getAbility())
          .mannerPoint(inviteEntity.getSenderUser().getStringAverageRating())
          .gameId(inviteEntity.getGame().getId())
          .build();
    }
  }

}
