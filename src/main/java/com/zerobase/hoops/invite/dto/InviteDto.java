package com.zerobase.hoops.invite.dto;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
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

public class InviteDto {

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CreateRequest {

    @NotNull(message = "받는 유저 아이디는 필수 입력 값 입니다.")
    @Min(1)
    private Long receiverUserId;

    @NotNull(message = "게임 아이디는 필수 입력 값 입니다.")
    @Min(1)
    private Long gameId;

    public static InviteEntity toEntity(
        UserEntity user,
        UserEntity receiverUser,
        GameEntity game) {
      return InviteEntity.builder()
          .inviteStatus(InviteStatus.REQUEST)
          .senderUserEntity(user)
          .receiverUserEntity(receiverUser)
          .gameEntity(game)
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CreateResponse {

    private Long inviteId;

    private InviteStatus inviteStatus;

    private LocalDateTime requestedDateTime;

    private String senderUserNickName;

    private String receiverUserNickName;

    private String title;

    public static CreateResponse toDto(InviteEntity inviteEntity) {
      return CreateResponse.builder()
          .inviteId(inviteEntity.getInviteId())
          .inviteStatus(inviteEntity.getInviteStatus())
          .requestedDateTime(inviteEntity.getRequestedDateTime())
          .senderUserNickName(inviteEntity.getSenderUserEntity().getNickName())
          .receiverUserNickName(inviteEntity.getReceiverUserEntity().getNickName())
          .title(inviteEntity.getGameEntity().getTitle())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CancelRequest {

    @NotNull(message = "초대 아이디는 필수 값 입니다.")
    @Min(1)
    private Long inviteId;

    @NotNull(message = "게임 아이디는 필수 입력 값 입니다.")
    @Min(1)
    private Long gameId;
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CancelResponse {

    private Long inviteId;

    private InviteStatus inviteStatus;

    private LocalDateTime requestedDateTime;

    private LocalDateTime canceledDateTime;

    private String senderUserNickName;

    private String receiverUserNickName;

    private String title;

    public static CancelResponse toDto(InviteEntity inviteEntity) {
      return CancelResponse.builder()
          .inviteId(inviteEntity.getInviteId())
          .inviteStatus(inviteEntity.getInviteStatus())
          .requestedDateTime(inviteEntity.getRequestedDateTime())
          .canceledDateTime(inviteEntity.getCanceledDateTime())
          .senderUserNickName(inviteEntity.getSenderUserEntity().getNickName())
          .receiverUserNickName(inviteEntity.getReceiverUserEntity().getNickName())
          .title(inviteEntity.getGameEntity().getTitle())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ReceiveAcceptRequest {
    @NotNull(message = "초대 아이디는 필수 값 입니다.")
    @Min(1)
    private Long inviteId;
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ReceiveAcceptResponse {
    private Long inviteId;

    private InviteStatus inviteStatus;

    private LocalDateTime requestedDateTime;

    private LocalDateTime acceptedDateTime;

    private String senderUserNickName;

    private String receiverUserNickName;

    private String title;

    public static ReceiveAcceptResponse toDto(InviteEntity inviteEntity) {
      return ReceiveAcceptResponse.builder()
          .inviteId(inviteEntity.getInviteId())
          .inviteStatus(inviteEntity.getInviteStatus())
          .requestedDateTime(inviteEntity.getRequestedDateTime())
          .acceptedDateTime(inviteEntity.getAcceptedDateTime())
          .senderUserNickName(inviteEntity.getSenderUserEntity().getNickName())
          .receiverUserNickName(inviteEntity.getReceiverUserEntity().getNickName())
          .title(inviteEntity.getGameEntity().getTitle())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ReceiveRejectRequest {
    @NotNull(message = "초대 아이디는 필수 값 입니다.")
    @Min(1)
    private Long inviteId;
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ReceiveRejectResponse {
    private Long inviteId;

    private InviteStatus inviteStatus;

    private LocalDateTime requestedDateTime;

    private LocalDateTime rejectedDateTime;

    private String senderUserNickName;

    private String receiverUserNickName;

    private String title;

    public static ReceiveRejectResponse toDto(InviteEntity inviteEntity) {
      return ReceiveRejectResponse.builder()
          .inviteId(inviteEntity.getInviteId())
          .inviteStatus(inviteEntity.getInviteStatus())
          .requestedDateTime(inviteEntity.getRequestedDateTime())
          .rejectedDateTime(inviteEntity.getRejectedDateTime())
          .senderUserNickName(inviteEntity.getSenderUserEntity().getNickName())
          .receiverUserNickName(inviteEntity.getReceiverUserEntity().getNickName())
          .title(inviteEntity.getGameEntity().getTitle())
          .build();
    }
  }

  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class InviteMyListResponse {
    private Long inviteId;

    private InviteStatus inviteStatus;

    private LocalDateTime requestedDateTime;

    private Long senderUserId;

    private LocalDate senderUserBirthday;

    private GenderType senderUserGenderType;

    private String senderUserNickName;

    private PlayStyleType senderUserPlayStyle;

    private AbilityType senderUserAbility;

    private String mannerPoint;

    private String receiverUserNickName;

    private Long gameId;

    private String title;

    private String content;

    private Long headCount;

    private FieldStatus fieldStatus;

    private Gender gender;

    private LocalDateTime startDateTime;

    private Boolean inviteYn;

    private String address;

    private Double latitude;

    private Double longitude;

    private CityName cityName;

    private MatchFormat matchFormat;



    public static InviteMyListResponse toDto(InviteEntity inviteEntity) {
      return InviteMyListResponse.builder()
          .inviteId(inviteEntity.getInviteId())
          .inviteStatus(inviteEntity.getInviteStatus())
          .requestedDateTime(inviteEntity.getRequestedDateTime())
          .senderUserId(inviteEntity.getSenderUserEntity().getId())
          .senderUserBirthday(inviteEntity.getSenderUserEntity().getBirthday())
          .senderUserGenderType(inviteEntity.getSenderUserEntity().getGender())
          .senderUserNickName(inviteEntity.getSenderUserEntity().getNickName())
          .senderUserPlayStyle(inviteEntity.getSenderUserEntity().getPlayStyle())
          .senderUserAbility(inviteEntity.getSenderUserEntity().getAbility())
          .mannerPoint(inviteEntity.getSenderUserEntity().getStringAverageRating())
          .receiverUserNickName(inviteEntity.getReceiverUserEntity().getNickName())
          .gameId(inviteEntity.getGameEntity().getGameId())
          .title(inviteEntity.getGameEntity().getTitle())
          .content(inviteEntity.getGameEntity().getContent())
          .headCount(inviteEntity.getGameEntity().getHeadCount())
          .fieldStatus(inviteEntity.getGameEntity().getFieldStatus())
          .gender(inviteEntity.getGameEntity().getGender())
          .startDateTime(inviteEntity.getGameEntity().getStartDateTime())
          .inviteYn(inviteEntity.getGameEntity().getInviteYn())
          .address(inviteEntity.getGameEntity().getAddress())
          .latitude(inviteEntity.getGameEntity().getLatitude())
          .longitude(inviteEntity.getGameEntity().getLongitude())
          .cityName(inviteEntity.getGameEntity().getCityName())
          .matchFormat(inviteEntity.getGameEntity().getMatchFormat())
          .build();
    }
  }

}
