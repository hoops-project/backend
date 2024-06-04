package com.zerobase.hoops.entity;

import jakarta.persistence.*;
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
}