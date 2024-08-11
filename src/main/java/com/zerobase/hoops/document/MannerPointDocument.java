package com.zerobase.hoops.document;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "index_chat_room")
public class MannerPointDocument {
  @Id
  private String id;

  @Field(type = FieldType.Integer)
  private int point;

  @Field(type = FieldType.Nested)
  private UserDocument user;

  @Field(type = FieldType.Nested)
  private UserDocument receiver;

  @Field(type = FieldType.Nested)
  private GameDocument game;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime createdDateTime;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MannerPointDocument that = (MannerPointDocument) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
