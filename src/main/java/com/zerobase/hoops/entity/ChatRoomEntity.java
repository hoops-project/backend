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


  @OneToOne
  @JoinColumn(name = "game_id")
  @Column(unique = true)
  private GameEntity gameEntity;

}