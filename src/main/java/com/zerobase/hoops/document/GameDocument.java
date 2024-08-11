package com.zerobase.hoops.document;

import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
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
@Document(indexName = "index_game")
public class GameDocument {

  @Id
  private String id;

  @Field(type = FieldType.Text)
  private String title;

  @Field(type = FieldType.Text)
  private String content;

  @Field(type = FieldType.Long)
  private Long headCount;

  @Field(type = FieldType.Keyword)
  private FieldStatus fieldStatus;

  @Field(type = FieldType.Keyword)
  private Gender gender;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime startDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime createdDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime deletedDateTime;

  @Field(type = FieldType.Boolean)
  private Boolean inviteYn;

  @Field(type = FieldType.Text)
  private String address;

  @Field(type = FieldType.Text)
  private String placeName;

  @Field(type = FieldType.Double)
  private Double latitude;

  @Field(type = FieldType.Double)
  private Double longitude;

  @Field(type = FieldType.Keyword)
  private CityName cityName;

  @Field(type = FieldType.Keyword)
  private MatchFormat matchFormat;

  @Field(type = FieldType.Nested)
  private UserDocument user;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GameDocument that = (GameDocument) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(title, that.title) &&
        Objects.equals(content, that.content) &&
        Objects.equals(headCount, that.headCount) &&
        Objects.equals(fieldStatus, that.fieldStatus) &&
        Objects.equals(gender, that.gender) &&
        Objects.equals(startDateTime, that.startDateTime) &&
        Objects.equals(createdDateTime, that.createdDateTime) &&
        Objects.equals(deletedDateTime, that.deletedDateTime) &&
        Objects.equals(inviteYn, that.inviteYn) &&
        Objects.equals(address, that.address) &&
        Objects.equals(placeName, that.placeName) &&
        Objects.equals(latitude, that.latitude) &&
        Objects.equals(longitude, that.longitude) &&
        Objects.equals(cityName, that.cityName) &&
        Objects.equals(matchFormat, that.matchFormat) &&
        Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, headCount, fieldStatus, gender,
        startDateTime, createdDateTime, deletedDateTime, inviteYn, address,
        placeName, latitude, longitude, cityName, matchFormat, user);
  }

}
