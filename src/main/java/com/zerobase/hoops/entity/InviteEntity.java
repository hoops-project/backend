package com.zerobase.hoops.entity;

import com.zerobase.hoops.invite.type.InviteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "invite")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class InviteEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long inviteId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InviteStatus inviteStatus;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime requestedDateTime;

  private LocalDateTime canceledDateTime;

  private LocalDateTime acceptedDateTime;

  private LocalDateTime deletedDateTime;


  @ManyToOne
  @JoinColumn(name = "sender_user_id", nullable = false)
  private UserEntity senderUserEntity;

  @ManyToOne
  @JoinColumn(name = "receiver_user_id", nullable = false)
  private UserEntity receiverUserEntity;

  @ManyToOne
  @JoinColumn(name = "game_id", nullable = false)
  private GameEntity gameEntity;

  public static InviteEntity toCancelEntity(InviteEntity inviteEntity) {
    return InviteEntity.builder()
        .inviteId(inviteEntity.getInviteId())
        .inviteStatus(InviteStatus.CANCEL)
        .requestedDateTime(inviteEntity.getRequestedDateTime())
        .canceledDateTime(LocalDateTime.now())
        .senderUserEntity(inviteEntity.getSenderUserEntity())
        .receiverUserEntity(inviteEntity.getReceiverUserEntity())
        .gameEntity(inviteEntity.getGameEntity())
        .build();
  }

  public static InviteEntity toAcceptEntity(InviteEntity inviteEntity) {
    return InviteEntity.builder()
        .inviteId(inviteEntity.getInviteId())
        .inviteStatus(InviteStatus.ACCEPT)
        .requestedDateTime(inviteEntity.getRequestedDateTime())
        .acceptedDateTime(LocalDateTime.now())
        .senderUserEntity(inviteEntity.getSenderUserEntity())
        .receiverUserEntity(inviteEntity.getReceiverUserEntity())
        .gameEntity(inviteEntity.getGameEntity())
        .build();
  }

}
