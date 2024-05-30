package com.zerobase.hoops.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "chatting_room")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "room_id")
  private Long roomId;

  @Builder.Default
  @Column(nullable = false)
  private Long sessionId = 0L;

  @OneToOne
  @JoinColumn(name = "game_id")
  private GameEntity gameEntity;

  public void saveGameInfo(GameEntity game){
    this.gameEntity = game;
  }
  public void changeNewSessionId(Long newSessionId){
    this.sessionId = newSessionId;
  }
}