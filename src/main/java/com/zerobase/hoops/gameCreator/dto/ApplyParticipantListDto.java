package com.zerobase.hoops.gameCreator.dto;

import com.zerobase.hoops.document.ParticipantGameDocument;
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

public class ApplyParticipantListDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    @Schema(description = "참여 pk", example = "2")
    private String participantId;

    @Schema(description = "경기 지원자 유저 생년월일", example = "2002-02-02")
    private LocalDate birthday;

    @Schema(description = "경기 지원자 유저 성별", example = "MALE")
    private GenderType gender;

    @Schema(description = "경기 지원자 유저 닉네임", example = "오리")
    private String nickName;

    @Schema(description = "경기 지원자 유저 플레이스타일", example = "AGGRESSIVE")
    private PlayStyleType playStyle;

    @Schema(description = "경기 지원자 유저 능력", example = "PASS")
    private AbilityType ability;

    @Schema(description = "경기 지원자 유저 매너점수", example = "3.5")
    private String mannerPoint;

    public static ApplyParticipantListDto.Response toDto(
        ParticipantGameDocument participantGameDocument){
      return ApplyParticipantListDto.Response.builder()
          .participantId(participantGameDocument.getId())
          .birthday(participantGameDocument.getUser().getBirthday())
          .gender(participantGameDocument.getUser().getGender())
          .nickName(participantGameDocument.getUser().getNickName())
          .playStyle(participantGameDocument.getUser().getPlayStyle())
          .ability(participantGameDocument.getUser().getAbility())
          .mannerPoint(participantGameDocument.getUser().getStringAverageRating())
          .build();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ApplyParticipantListDto.Response that = (ApplyParticipantListDto.Response) o;
      return Objects.equals(participantId, that.participantId) &&
          Objects.equals(birthday, that.birthday) &&
          Objects.equals(gender, that.gender) &&
          Objects.equals(nickName, that.nickName) &&
          Objects.equals(playStyle, that.playStyle) &&
          Objects.equals(ability, that.ability) &&
          Objects.equals(mannerPoint, that.mannerPoint);
    }

    @Override
    public int hashCode() {
      return Objects.hash(participantId, birthday, gender, nickName,
          playStyle, ability, mannerPoint);
    }

  }

}
