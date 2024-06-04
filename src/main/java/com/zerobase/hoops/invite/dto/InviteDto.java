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
          .senderUser(user)
          .receiverUser(receiverUser)
          .game(game)
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
          .inviteId(inviteEntity.getId())
          .inviteStatus(inviteEntity.getInviteStatus())
          .requestedDateTime(inviteEntity.getRequestedDateTime())
          .senderUserNickName(inviteEntity.getSenderUser().getNickName())
          .receiverUserNickName(inviteEntity.getReceiverUser().getNickName())
          .title(inviteEntity.getGame().getTitle())
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
          .inviteId(inviteEntity.getId())
          .inviteStatus(inviteEntity.getInviteStatus())
          .requestedDateTime(inviteEntity.getRequestedDateTime())
          .canceledDateTime(inviteEntity.getCanceledDateTime())
          .senderUserNickName(inviteEntity.getSenderUser().getNickName())
          .receiverUserNickName(inviteEntity.getReceiverUser().getNickName())
          .title(inviteEntity.getGame().getTitle())
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
          .inviteId(inviteEntity.getId())
          .inviteStatus(inviteEntity.getInviteStatus())
          .requestedDateTime(inviteEntity.getRequestedDateTime())
          .acceptedDateTime(inviteEntity.getAcceptedDateTime())
          .senderUserNickName(inviteEntity.getSenderUser().getNickName())
          .receiverUserNickName(inviteEntity.getReceiverUser().getNickName())
          .title(inviteEntity.getGame().getTitle())
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
          .inviteId(inviteEntity.getId())
          .inviteStatus(inviteEntity.getInviteStatus())
          .requestedDateTime(inviteEntity.getRequestedDateTime())
          .rejectedDateTime(inviteEntity.getRejectedDateTime())
          .senderUserNickName(inviteEntity.getSenderUser().getNickName())
          .receiverUserNickName(inviteEntity.getReceiverUser().getNickName())
          .title(inviteEntity.getGame().getTitle())
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
          .inviteId(inviteEntity.getId())
          .inviteStatus(inviteEntity.getInviteStatus())
          .requestedDateTime(inviteEntity.getRequestedDateTime())
          .senderUserId(inviteEntity.getSenderUser().getId())
          .senderUserBirthday(inviteEntity.getSenderUser().getBirthday())
          .senderUserGenderType(inviteEntity.getSenderUser().getGender())
          .senderUserNickName(inviteEntity.getSenderUser().getNickName())
          .senderUserPlayStyle(inviteEntity.getSenderUser().getPlayStyle())
          .senderUserAbility(inviteEntity.getSenderUser().getAbility())
          .mannerPoint(inviteEntity.getSenderUser().getStringAverageRating())
          .receiverUserNickName(inviteEntity.getReceiverUser().getNickName())
          .gameId(inviteEntity.getGame().getId())
          .title(inviteEntity.getGame().getTitle())
          .content(inviteEntity.getGame().getContent())
          .headCount(inviteEntity.getGame().getHeadCount())
          .fieldStatus(inviteEntity.getGame().getFieldStatus())
          .gender(inviteEntity.getGame().getGender())
          .startDateTime(inviteEntity.getGame().getStartDateTime())
          .inviteYn(inviteEntity.getGame().getInviteYn())
          .address(inviteEntity.getGame().getAddress())
          .latitude(inviteEntity.getGame().getLatitude())
          .longitude(inviteEntity.getGame().getLongitude())
          .cityName(inviteEntity.getGame().getCityName())
          .matchFormat(inviteEntity.getGame().getMatchFormat())
          .build();
    }
  }

}
