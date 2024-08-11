package com.zerobase.hoops.document;

import com.zerobase.hoops.invite.type.InviteStatus;
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
@Document(indexName = "index_invite")
public class InviteDocument {

  @Id
  private String id;

  @Field(type = FieldType.Keyword)
  private InviteStatus inviteStatus;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime requestedDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime canceledDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime acceptedDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime rejectedDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime deletedDateTime;

  @Field(type = FieldType.Nested)
  private UserDocument senderUser;

  @Field(type = FieldType.Nested)
  private UserDocument receiverUser;

  @Field(type = FieldType.Nested)
  private GameDocument game;

  public void assignSenderUser(UserDocument user) {
    this.senderUser = user;
  }

  public void assignReceiverUser(UserDocument user) {
    this.receiverUser = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InviteDocument that = (InviteDocument) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(inviteStatus, that.inviteStatus) &&
        Objects.equals(requestedDateTime, that.requestedDateTime) &&
        Objects.equals(canceledDateTime, that.canceledDateTime) &&
        Objects.equals(acceptedDateTime, that.acceptedDateTime) &&
        Objects.equals(rejectedDateTime, that.rejectedDateTime) &&
        Objects.equals(deletedDateTime, that.deletedDateTime) &&
        Objects.equals(senderUser, that.senderUser) &&
        Objects.equals(receiverUser, that.receiverUser) &&
        Objects.equals(game, that.game);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, inviteStatus,
        requestedDateTime, canceledDateTime, acceptedDateTime,
        rejectedDateTime, deletedDateTime, senderUser, receiverUser, game);
  }

  public static InviteDocument toCancelDocument(InviteDocument inviteDocument,
      Clock clock) {
    return InviteDocument.builder()
        .id(inviteDocument.getId())
        .inviteStatus(InviteStatus.CANCEL)
        .requestedDateTime(inviteDocument.getRequestedDateTime())
        .canceledDateTime(getOffsetTime(clock))
        .senderUser(inviteDocument.getSenderUser())
        .receiverUser(inviteDocument.getReceiverUser())
        .game(inviteDocument.getGame())
        .build();
  }

  public static InviteDocument toAcceptDocument(InviteDocument inviteDocument,
      OffsetDateTime nowDateTime) {
    return InviteDocument.builder()
        .id(inviteDocument.getId())
        .inviteStatus(InviteStatus.ACCEPT)
        .requestedDateTime(inviteDocument.getRequestedDateTime())
        .acceptedDateTime(nowDateTime)
        .senderUser(inviteDocument.getSenderUser())
        .receiverUser(inviteDocument.getReceiverUser())
        .game(inviteDocument.getGame())
        .build();
  }

  public static InviteDocument toRejectDocument(InviteDocument inviteDocument,
      Clock clock) {
    return InviteDocument.builder()
        .id(inviteDocument.getId())
        .inviteStatus(InviteStatus.REJECT)
        .requestedDateTime(inviteDocument.getRequestedDateTime())
        .rejectedDateTime(getOffsetTime(clock))
        .senderUser(inviteDocument.getSenderUser())
        .receiverUser(inviteDocument.getReceiverUser())
        .game(inviteDocument.getGame())
        .build();
  }

  public static InviteDocument setCancel(InviteDocument inviteDocument,
      Clock clock) {
    return InviteDocument.builder()
        .id(inviteDocument.getId())
        .inviteStatus(InviteStatus.CANCEL)
        .requestedDateTime(inviteDocument.getRequestedDateTime())
        .canceledDateTime(getOffsetTime(clock))
        .senderUser(inviteDocument.getSenderUser())
        .receiverUser(inviteDocument.getReceiverUser())
        .game(inviteDocument.getGame())
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

}
