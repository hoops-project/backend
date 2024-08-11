package com.zerobase.hoops.document;

import com.zerobase.hoops.friends.type.FriendStatus;
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
@Document(indexName = "index_friend")
public class FriendDocument {

  @Id
  private String id;

  @Field(type = FieldType.Keyword)
  private FriendStatus status;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime createdDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime acceptedDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime rejectedDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime canceledDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime deletedDateTime;

  @Field(type = FieldType.Nested)
  private UserDocument user;

  @Field(type = FieldType.Nested)
  private UserDocument friendUser;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FriendDocument that = (FriendDocument) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(status, that.status) &&
        Objects.equals(createdDateTime, that.createdDateTime) &&
        Objects.equals(acceptedDateTime, that.acceptedDateTime) &&
        Objects.equals(rejectedDateTime, that.rejectedDateTime) &&
        Objects.equals(canceledDateTime, that.canceledDateTime) &&
        Objects.equals(deletedDateTime, that.deletedDateTime) &&
        Objects.equals(user, that.user) &&
        Objects.equals(friendUser, that.friendUser);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, status, createdDateTime, acceptedDateTime,
        rejectedDateTime, canceledDateTime, deletedDateTime, user, friendUser);
  }

  public static FriendDocument setCancel(FriendDocument friendDocument,
      Clock clock) {

    return FriendDocument.builder()
        .id(friendDocument.getId())
        .status(FriendStatus.CANCEL)
        .createdDateTime(friendDocument.getCreatedDateTime())
        .canceledDateTime(getOffsetTime(clock))
        .user(friendDocument.getUser())
        .friendUser(friendDocument.getFriendUser())
        .build();
  }

  public static FriendDocument setAcceptMyFriend(FriendDocument friendDocument,
      Clock clock) {
    return FriendDocument.builder()
        .id(friendDocument.getId())
        .status(FriendStatus.ACCEPT)
        .createdDateTime(friendDocument.getCreatedDateTime())
        .acceptedDateTime(getOffsetTime(clock))
        .user(friendDocument.getUser())
        .friendUser(friendDocument.getFriendUser())
        .build();
  }

  public static FriendDocument setAcceptOtherFriend(FriendDocument friendDocument,
      long friendId) {
    return FriendDocument.builder()
        .id(Long.toString(friendId))
        .status(FriendStatus.ACCEPT)
        .createdDateTime(friendDocument.getCreatedDateTime())
        .acceptedDateTime(friendDocument.getAcceptedDateTime())
        .user(friendDocument.getFriendUser())
        .friendUser(friendDocument.getUser())
        .build();
  }

  public static FriendDocument setReject(FriendDocument friendDocument,
      Clock clock) {
    return FriendDocument.builder()
        .id(friendDocument.getId())
        .status(FriendStatus.REJECT)
        .createdDateTime(friendDocument.getCreatedDateTime())
        .rejectedDateTime(getOffsetTime(clock))
        .user(friendDocument.getUser())
        .friendUser(friendDocument.getFriendUser())
        .build();
  }

  public static FriendDocument setDeleteMyFriend(FriendDocument friendDocument,
      Clock clock) {
    return FriendDocument.builder()
        .id(friendDocument.getId())
        .status(FriendStatus.DELETE)
        .createdDateTime(friendDocument.getCreatedDateTime())
        .acceptedDateTime(friendDocument.getAcceptedDateTime())
        .deletedDateTime(getOffsetTime(clock))
        .user(friendDocument.getUser())
        .friendUser(friendDocument.getFriendUser())
        .build();
  }

  public static FriendDocument setDeleteOtherFriend(
      FriendDocument selfFriendDocument,
      FriendDocument otherFriendDocument) {
    return FriendDocument.builder()
        .id(otherFriendDocument.getId())
        .status(FriendStatus.DELETE)
        .createdDateTime(otherFriendDocument.getCreatedDateTime())
        .acceptedDateTime(otherFriendDocument.getAcceptedDateTime())
        .deletedDateTime(selfFriendDocument.getDeletedDateTime())
        .user(otherFriendDocument.getUser())
        .friendUser(otherFriendDocument.getFriendUser())
        .build();
  }

  public static OffsetDateTime getOffsetTime(Clock clock) {
    // 포맷 정의 (타임존 오프셋 포함)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    // Clock을 사용하여 ZonedDateTime을 생성합니다.
    ZonedDateTime zonedDateTime = ZonedDateTime.now(clock);

    // ZonedDateTime을 OffsetDateTime으로 변환합니다.
    OffsetDateTime offsetDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.ofHours(9))
        .toOffsetDateTime();

    // 문자열로 포맷
    String formattedDateTime = offsetDateTime.format(formatter);

    // 문자열을 OffsetDateTime으로 파싱
    return OffsetDateTime.parse(formattedDateTime, formatter);
  }

}
