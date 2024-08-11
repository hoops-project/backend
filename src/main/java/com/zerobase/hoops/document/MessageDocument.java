package com.zerobase.hoops.document;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "index_message")
public class MessageDocument {

  @Id
  private String id;

  @Field(type = FieldType.Text)
  private String content;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime sendDateTime;

  @Field(type = FieldType.Nested)
  private UserDocument user;

  @Field(type = FieldType.Nested)
  private ChatRoomDocument chatRoom;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MessageDocument that = (MessageDocument) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
