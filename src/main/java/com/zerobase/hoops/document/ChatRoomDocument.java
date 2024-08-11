package com.zerobase.hoops.document;

import jakarta.persistence.Id;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "index_chat_room")
public class ChatRoomDocument {

  @Id
  private String id;

  @Field(type = FieldType.Nested)
  private GameDocument gameDocument;

  @Field(type = FieldType.Nested)
  private UserDocument userDocument;

  public void saveId(long id) {
    this.id = Long.toString(id);
  }

  public void saveGameInfo(GameDocument game){
    this.gameDocument = game;
  }
  public void saveUserInfo(UserDocument user){
    this.userDocument = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChatRoomDocument that = (ChatRoomDocument) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}