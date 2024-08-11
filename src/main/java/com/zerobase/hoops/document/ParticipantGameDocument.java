package com.zerobase.hoops.document;

import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import jakarta.persistence.Id;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "index_participant_game")
public class ParticipantGameDocument {

  @Id
  private String id;

  @Field(type = FieldType.Keyword)
  private ParticipantGameStatus status;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime createdDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime acceptedDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime rejectedDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime canceledDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime withdrewDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime kickoutDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime deletedDateTime;

  @Field(type = FieldType.Nested)
  private GameDocument game;

  @Field(type = FieldType.Nested)
  private UserDocument user;

  public ParticipantGameDocument toGameCreatorDocument(
      GameDocument game,
      UserDocument user,
      long participantId) {

    return ParticipantGameDocument.builder()
        .id(Long.toString(participantId))
        .status(ParticipantGameStatus.ACCEPT)
        .createdDateTime(game.getCreatedDateTime())
        .acceptedDateTime(game.getCreatedDateTime())
        .game(game)
        .user(user)
        .build();
  }

  public ParticipantGameDocument setAccept(ParticipantGameDocument entity,
      Clock clock) {
    return ParticipantGameDocument.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.ACCEPT)
        .createdDateTime(entity.getCreatedDateTime())
        .acceptedDateTime(getOffsetTime(clock))
        .game(entity.getGame())
        .user(entity.getUser())
        .build();
  }

  public ParticipantGameDocument setReject(ParticipantGameDocument entity,
      Clock clock) {
    return ParticipantGameDocument.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.REJECT)
        .createdDateTime(entity.getCreatedDateTime())
        .rejectedDateTime(getOffsetTime(clock))
        .game(entity.getGame())
        .user(entity.getUser())
        .build();
  }

  public ParticipantGameDocument setKickout(ParticipantGameDocument entity,
      Clock clock) {
    return ParticipantGameDocument.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.KICKOUT)
        .createdDateTime(entity.getCreatedDateTime())
        .acceptedDateTime(entity.getAcceptedDateTime())
        .kickoutDateTime(getOffsetTime(clock))
        .game(entity.getGame())
        .user(entity.getUser())
        .build();
  }

  public ParticipantGameDocument setWithdraw(ParticipantGameDocument entity,
      Clock clock) {
    return ParticipantGameDocument.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.WITHDRAW)
        .createdDateTime(entity.getCreatedDateTime())
        .acceptedDateTime(entity.getAcceptedDateTime())
        .withdrewDateTime(getOffsetTime(clock))
        .game(entity.getGame())
        .user(entity.getUser())
        .build();
  }

  public ParticipantGameDocument setDelete(ParticipantGameDocument entity,
      Clock clock) {
    return ParticipantGameDocument.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.DELETE)
        .createdDateTime(entity.getCreatedDateTime())
        .acceptedDateTime(entity.getAcceptedDateTime())
        .deletedDateTime(getOffsetTime(clock))
        .game(entity.getGame())
        .user(entity.getUser())
        .build();
  }

  public ParticipantGameDocument gameCreatorInvite(InviteDocument inviteDocument,
      OffsetDateTime nowDateTime,
      long participantGameId) {
    return ParticipantGameDocument.builder()
            .id(Long.toString(participantGameId))
            .status(ParticipantGameStatus.ACCEPT)
            .createdDateTime(nowDateTime)
            .acceptedDateTime(nowDateTime)
            .game(inviteDocument.getGame())
            .user(inviteDocument.getReceiverUser())
            .build();
  }

  public ParticipantGameDocument gameUserInvite(InviteDocument inviteDocument,
      OffsetDateTime nowDateTime,
      long participantGameId) {
    return ParticipantGameDocument.builder()
        .id(Long.toString(participantGameId))
        .status(ParticipantGameStatus.APPLY)
        .createdDateTime(nowDateTime)
        .game(inviteDocument.getGame())
        .user(inviteDocument.getReceiverUser())
        .build();
  }

  public static OffsetDateTime getOffsetTime(Clock clock) {
    // 포맷 정의 (타임존 오프셋 포함)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    // Clock을 사용하여 ZonedDateTime을 생성합니다.
    ZonedDateTime zonedDateTime = ZonedDateTime.now(clock);

    // ZonedDateTime을 OffsetDateTime으로 변환합니다.
    OffsetDateTime offsetDateTime = zonedDateTime.withZoneSameInstant(
            ZoneOffset.ofHours(9))
        .toOffsetDateTime();

    // 문자열로 포맷
    String formattedDateTime = offsetDateTime.format(formatter);

    // 문자열을 OffsetDateTime으로 파싱
    return OffsetDateTime.parse(formattedDateTime, formatter);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ParticipantGameDocument that = (ParticipantGameDocument) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(status, that.status) &&
        Objects.equals(createdDateTime, that.createdDateTime) &&
        Objects.equals(acceptedDateTime, that.acceptedDateTime) &&
        Objects.equals(rejectedDateTime, that.rejectedDateTime) &&
        Objects.equals(canceledDateTime, that.canceledDateTime) &&
        Objects.equals(withdrewDateTime, that.withdrewDateTime) &&
        Objects.equals(kickoutDateTime, that.kickoutDateTime) &&
        Objects.equals(deletedDateTime, that.deletedDateTime) &&
        Objects.equals(game, that.game) &&
        Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, status, createdDateTime, acceptedDateTime,
        rejectedDateTime, canceledDateTime, withdrewDateTime, kickoutDateTime,
        deletedDateTime, game, user);
  }

}
