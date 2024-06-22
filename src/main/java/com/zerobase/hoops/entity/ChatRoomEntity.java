package com.zerobase.hoops.entity;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.*;

@Entity(name = "chat_room")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private GameEntity gameEntity;

  @ManyToOne
  private UserEntity userEntity;

  public void saveGameInfo(GameEntity game){
    this.gameEntity = game;
  }
  public void saveUserInfo(UserEntity user){
    this.userEntity = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChatRoomEntity that = (ChatRoomEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}