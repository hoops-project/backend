package com.zerobase.hoops.document;

import com.zerobase.hoops.alarm.domain.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "index_notification")
public class NotificationDocument {

  @Id
  private String id;

  @Field(type = FieldType.Nested)
  private UserDocument receiver;

  @Field(type = FieldType.Keyword)
  private NotificationType notificationType;

  @Field(type = FieldType.Text)
  private String content;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime createdDateTime;

}
