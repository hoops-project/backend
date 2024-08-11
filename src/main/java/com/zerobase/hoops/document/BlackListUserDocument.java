package com.zerobase.hoops.document;

import jakarta.persistence.Id;
import java.time.LocalDate;
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
@Document(indexName = "index_blacklist_user")
public class BlackListUserDocument {

  @Id
  private String id;

  @Field(type = FieldType.Nested)
  private UserDocument blackUser;

  @Field(type = FieldType.Date, format = DateFormat.date)
  private LocalDate startDate;

  @Field(type = FieldType.Date, format = DateFormat.date)
  private LocalDate endDate;

  public void unLockBlackList() {
    this.endDate = LocalDate.now().minusDays(1);
  }
}
