package com.zerobase.hoops.gameCreator.dto;

import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class ParticipantDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DetailResponse {
    private Long participantId;

    private ParticipantGameStatus status;

    private LocalDateTime createdDateTime;

    private GenderType gender;

    private String nickName;

    private PlayStyleType playStyle;

    private AbilityType ability;

    private String mannerPoint;

    private Long userId;

    public static ParticipantDto.DetailResponse toDto(
        ParticipantGameEntity participantGameEntity){
      return DetailResponse.builder()
          .participantId(participantGameEntity.getParticipantId())
          .status(participantGameEntity.getStatus())
          .createdDateTime(participantGameEntity.getCreatedDateTime())
          .gender(participantGameEntity.getUserEntity().getGender())
          .nickName(participantGameEntity.getUserEntity().getNickName())
          .playStyle(participantGameEntity.getUserEntity().getPlayStyle())
          .ability(participantGameEntity.getUserEntity().getAbility())
          .mannerPoint(participantGameEntity.getUserEntity().getStringAverageRating())
          .userId(participantGameEntity.getUserEntity().getUserId())
          .build();
    }

    // 테스트 코드용 List 간 equals
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DetailResponse that = (DetailResponse) o;
      return Objects.equals(participantId, that.participantId) &&
          Objects.equals(status, that.status) &&
          Objects.equals(createdDateTime, that.createdDateTime) &&
          Objects.equals(gender, that.gender) &&
          Objects.equals(nickName, that.nickName) &&
          Objects.equals(playStyle, that.playStyle) &&
          Objects.equals(ability, that.ability);
    }

  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AcceptRequest {
    @NotNull(message = "참가 아이디는 필수 값입니다.")
    @Min(1)
    private Long participantId;
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AcceptResponse {

    private Long participantId;

    private ParticipantGameStatus status;

    private LocalDateTime createdDateTime;

    private LocalDateTime acceptedDateTime;

    private Long userId;

    public static ParticipantDto.AcceptResponse toDto(
        ParticipantGameEntity participantGameEntity) {
      return AcceptResponse.builder()
          .participantId(participantGameEntity.getParticipantId())
          .status(participantGameEntity.getStatus())
          .createdDateTime(participantGameEntity.getCreatedDateTime())
          .acceptedDateTime(participantGameEntity.getAcceptedDateTime())
          .userId(participantGameEntity.getUserEntity().getUserId()).build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RejectRequest {
    @NotNull(message = "참가 아이디는 필수 값입니다.")
    @Min(1)
    private Long participantId;
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RejectResponse {

    private Long participantId;

    private ParticipantGameStatus status;

    private LocalDateTime createdDateTime;

    private LocalDateTime rejectedDateTime;

    private Long userId;

    public static ParticipantDto.RejectResponse toDto(
        ParticipantGameEntity participantGameEntity) {
      return RejectResponse.builder()
          .participantId(participantGameEntity.getParticipantId())
          .status(participantGameEntity.getStatus())
          .createdDateTime(participantGameEntity.getCreatedDateTime())
          .rejectedDateTime(participantGameEntity.getRejectedDateTime())
          .userId(participantGameEntity.getUserEntity().getUserId()).build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class KickoutRequest {
    @NotNull(message = "참가 아이디는 필수 값입니다.")
    @Min(1)
    private Long participantId;
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class KickoutResponse {

    private Long participantId;

    private ParticipantGameStatus status;

    private LocalDateTime createdDateTime;

    private LocalDateTime acceptedDateTime;

    private LocalDateTime kickoutDateTime;

    private Long userId;

    public static ParticipantDto.KickoutResponse toDto(
        ParticipantGameEntity participantGameEntity) {
      return KickoutResponse.builder()
          .participantId(participantGameEntity.getParticipantId())
          .status(participantGameEntity.getStatus())
          .createdDateTime(participantGameEntity.getCreatedDateTime())
          .acceptedDateTime(participantGameEntity.getAcceptedDateTime())
          .kickoutDateTime(participantGameEntity.getKickoutDateTime())
          .userId(participantGameEntity.getUserEntity().getUserId()).build();
    }
  }

}
