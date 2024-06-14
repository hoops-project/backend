package com.zerobase.hoops.entity;

import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
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

@Entity(name = "participant_game")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ParticipantGameEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ParticipantGameStatus status;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdDateTime;

  private LocalDateTime acceptedDateTime;

  private LocalDateTime rejectedDateTime;

  private LocalDateTime canceledDateTime;

  private LocalDateTime withdrewDateTime;

  private LocalDateTime kickoutDateTime;

  private LocalDateTime deletedDateTime;

  @ManyToOne
  @JoinColumn(nullable = false)
  private GameEntity game;

  @ManyToOne
  @JoinColumn(nullable = false)
  private UserEntity user;

  public static ParticipantGameEntity toGameCreatorEntity(
      GameEntity gameEntity,
      UserEntity userEntity,
      Clock clock) {
    return ParticipantGameEntity.builder()
        .status(ParticipantGameStatus.ACCEPT)
        .acceptedDateTime(LocalDateTime.now(clock))
        .game(gameEntity)
        .user(userEntity)
        .build();
  }

  public static ParticipantGameEntity setAccept(ParticipantGameEntity entity) {
    return ParticipantGameEntity.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.ACCEPT)
        .createdDateTime(entity.getCreatedDateTime())
        .acceptedDateTime(LocalDateTime.now())
        .game(entity.getGame())
        .user(entity.getUser())
        .build();
  }

  public static ParticipantGameEntity setReject(ParticipantGameEntity entity) {
    return ParticipantGameEntity.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.REJECT)
        .createdDateTime(entity.getCreatedDateTime())
        .rejectedDateTime(LocalDateTime.now())
        .game(entity.getGame())
        .user(entity.getUser())
        .build();
  }

  public static ParticipantGameEntity setKickout(ParticipantGameEntity entity) {
    return ParticipantGameEntity.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.KICKOUT)
        .createdDateTime(entity.getCreatedDateTime())
        .acceptedDateTime(entity.getAcceptedDateTime())
        .kickoutDateTime(LocalDateTime.now())
        .game(entity.getGame())
        .user(entity.getUser())
        .build();
  }

  public static ParticipantGameEntity setWithdraw(ParticipantGameEntity entity,
      Clock clock) {
    return ParticipantGameEntity.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.WITHDRAW)
        .createdDateTime(entity.getCreatedDateTime())
        .acceptedDateTime(entity.getAcceptedDateTime())
        .withdrewDateTime(LocalDateTime.now(clock))
        .game(entity.getGame())
        .user(entity.getUser())
        .build();
  }

  public static ParticipantGameEntity setDelete(ParticipantGameEntity entity,
      Clock clock) {
    return ParticipantGameEntity.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.DELETE)
        .createdDateTime(entity.getCreatedDateTime())
        .acceptedDateTime(entity.getAcceptedDateTime())
        .deletedDateTime(LocalDateTime.now(clock))
        .game(entity.getGame())
        .user(entity.getUser())
        .build();
  }

  public static ParticipantGameEntity gameCreatorInvite(InviteEntity inviteEntity,
      LocalDateTime nowDateTime) {
    return ParticipantGameEntity.builder()
            .status(ParticipantGameStatus.ACCEPT)
            .createdDateTime(nowDateTime)
            .acceptedDateTime(nowDateTime)
            .game(inviteEntity.getGame())
            .user(inviteEntity.getReceiverUser())
            .build();
  }

  public static ParticipantGameEntity gameUserInvite(InviteEntity inviteEntity) {
    return ParticipantGameEntity.builder()
        .status(ParticipantGameStatus.APPLY)
        .game(inviteEntity.getGame())
        .user(inviteEntity.getReceiverUser())
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ParticipantGameEntity that = (ParticipantGameEntity) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(status, that.status) &&
        Objects.equals(createdDateTime, that.createdDateTime) &&
        Objects.equals(acceptedDateTime, that.acceptedDateTime) &&
        Objects.equals(rejectedDateTime, that.rejectedDateTime) &&
        Objects.equals(canceledDateTime, that.canceledDateTime) &&
        Objects.equals(withdrewDateTime, that.withdrewDateTime) &&
        Objects.equals(kickoutDateTime, that.kickoutDateTime) &&
        Objects.equals(deletedDateTime, that.deletedDateTime) &&
        Objects.equals(game, that.game) &&
        Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, status, createdDateTime, acceptedDateTime,
        rejectedDateTime, canceledDateTime, withdrewDateTime, kickoutDateTime,
        deletedDateTime, game, user);
  }

}
