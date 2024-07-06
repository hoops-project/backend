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
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
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

  public void assignSenderUser(UserEntity user) {
    this.senderUser = user;
  }

  public void assignReceiverUser(UserEntity user) {
    this.receiverUser = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InviteEntity that = (InviteEntity) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(inviteStatus, that.inviteStatus) &&
        Objects.equals(requestedDateTime, that.requestedDateTime) &&
        Objects.equals(canceledDateTime, that.canceledDateTime) &&
        Objects.equals(acceptedDateTime, that.acceptedDateTime) &&
        Objects.equals(rejectedDateTime, that.rejectedDateTime) &&
        Objects.equals(deletedDateTime, that.deletedDateTime) &&
        Objects.equals(senderUser, that.senderUser) &&
        Objects.equals(receiverUser, that.receiverUser) &&
        Objects.equals(game, that.game);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, inviteStatus,
        requestedDateTime, canceledDateTime, acceptedDateTime,
        rejectedDateTime, deletedDateTime, senderUser, receiverUser, game);
  }

  public static InviteEntity toCancelEntity(InviteEntity inviteEntity,
      Clock clock) {
    return InviteEntity.builder()
        .id(inviteEntity.getId())
        .inviteStatus(InviteStatus.CANCEL)
        .requestedDateTime(inviteEntity.getRequestedDateTime())
        .canceledDateTime(LocalDateTime.now(clock))
        .senderUser(inviteEntity.getSenderUser())
        .receiverUser(inviteEntity.getReceiverUser())
        .game(inviteEntity.getGame())
        .build();
  }

  public static InviteEntity toAcceptEntity(InviteEntity inviteEntity,
      LocalDateTime nowDateTime) {
    return InviteEntity.builder()
        .id(inviteEntity.getId())
        .inviteStatus(InviteStatus.ACCEPT)
        .requestedDateTime(inviteEntity.getRequestedDateTime())
        .acceptedDateTime(nowDateTime)
        .senderUser(inviteEntity.getSenderUser())
        .receiverUser(inviteEntity.getReceiverUser())
        .game(inviteEntity.getGame())
        .build();
  }

  public static InviteEntity toRejectEntity(InviteEntity inviteEntity,
      Clock clock) {
    return InviteEntity.builder()
        .id(inviteEntity.getId())
        .inviteStatus(InviteStatus.REJECT)
        .requestedDateTime(inviteEntity.getRequestedDateTime())
        .rejectedDateTime(LocalDateTime.now(clock))
        .senderUser(inviteEntity.getSenderUser())
        .receiverUser(inviteEntity.getReceiverUser())
        .game(inviteEntity.getGame())
        .build();
  }

  public static InviteEntity setCancel(InviteEntity inviteEntity,
      Clock clock) {
    return InviteEntity.builder()
        .id(inviteEntity.getId())
        .inviteStatus(InviteStatus.CANCEL)
        .requestedDateTime(inviteEntity.getRequestedDateTime())
        .canceledDateTime(LocalDateTime.now(clock))
        .senderUser(inviteEntity.getSenderUser())
        .receiverUser(inviteEntity.getReceiverUser())
        .game(inviteEntity.getGame())
        .build();
  }

}
