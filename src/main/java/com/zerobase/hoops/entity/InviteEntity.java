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
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "invite")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class InviteEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InviteStatus inviteStatus;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime requestedDateTime;

  private LocalDateTime canceledDateTime;

  private LocalDateTime acceptedDateTime;

  private LocalDateTime rejectedDateTime;

  private LocalDateTime deletedDateTime;

  @ManyToOne
  @JoinColumn(nullable = false)
  private UserEntity senderUser;

  @ManyToOne
  @JoinColumn(nullable = false)
  private UserEntity receiverUser;

  @ManyToOne
  @JoinColumn(nullable = false)
  private GameEntity game;

  public static InviteEntity toCancelEntity(InviteEntity inviteEntity) {
    return InviteEntity.builder()
        .id(inviteEntity.getId())
        .inviteStatus(InviteStatus.CANCEL)
        .requestedDateTime(inviteEntity.getRequestedDateTime())
        .canceledDateTime(LocalDateTime.now())
        .senderUser(inviteEntity.getSenderUser())
        .receiverUser(inviteEntity.getReceiverUser())
        .game(inviteEntity.getGame())
        .build();
  }

  public static InviteEntity toAcceptEntity(InviteEntity inviteEntity) {
    return InviteEntity.builder()
        .id(inviteEntity.getId())
        .inviteStatus(InviteStatus.ACCEPT)
        .requestedDateTime(inviteEntity.getRequestedDateTime())
        .acceptedDateTime(LocalDateTime.now())
        .senderUser(inviteEntity.getSenderUser())
        .receiverUser(inviteEntity.getReceiverUser())
        .game(inviteEntity.getGame())
        .build();
  }

  public static InviteEntity toRejectEntity(InviteEntity inviteEntity) {
    return InviteEntity.builder()
        .id(inviteEntity.getId())
        .inviteStatus(InviteStatus.REJECT)
        .requestedDateTime(inviteEntity.getRequestedDateTime())
        .rejectedDateTime(LocalDateTime.now())
        .senderUser(inviteEntity.getSenderUser())
        .receiverUser(inviteEntity.getReceiverUser())
        .game(inviteEntity.getGame())
        .build();
  }

}
