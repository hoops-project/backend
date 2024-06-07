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

  @Builder.Default
  @Column(nullable = false)
  private Long sessionId = 0L;

  @OneToOne
  private GameEntity gameEntity;

  public void saveGameInfo(GameEntity game){
    this.gameEntity = game;
  }
  public void changeNewSessionId(Long newSessionId){
    this.sessionId = newSessionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChatRoomEntity that = (ChatRoomEntity) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(gameEntity, that.gameEntity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, gameEntity);
  }
}