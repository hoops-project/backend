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
import java.time.LocalDateTime;
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
  private GameEntity gameEntity;

  @ManyToOne
  @JoinColumn(nullable = false)
  private UserEntity userEntity;

  public static ParticipantGameEntity toGameCreatorEntity(
      GameEntity gameEntity,
      UserEntity userEntity) {
    return ParticipantGameEntity.builder()
        .status(ParticipantGameStatus.ACCEPT)
        .createdDateTime(gameEntity.getCreatedDateTime())
        .acceptedDateTime(gameEntity.getCreatedDateTime())
        .gameEntity(gameEntity)
        .userEntity(userEntity)
        .build();
  }

  public static ParticipantGameEntity setAccept(ParticipantGameEntity entity) {
    return ParticipantGameEntity.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.ACCEPT)
        .createdDateTime(entity.getCreatedDateTime())
        .acceptedDateTime(LocalDateTime.now())
        .gameEntity(entity.getGameEntity())
        .userEntity(entity.getUserEntity())
        .build();
  }

  public static ParticipantGameEntity setReject(ParticipantGameEntity entity) {
    return ParticipantGameEntity.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.REJECT)
        .createdDateTime(entity.getCreatedDateTime())
        .rejectedDateTime(LocalDateTime.now())
        .gameEntity(entity.getGameEntity())
        .userEntity(entity.getUserEntity())
        .build();
  }

  public static ParticipantGameEntity setKickout(ParticipantGameEntity entity) {
    return ParticipantGameEntity.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.KICKOUT)
        .createdDateTime(entity.getCreatedDateTime())
        .acceptedDateTime(entity.getAcceptedDateTime())
        .kickoutDateTime(LocalDateTime.now())
        .gameEntity(entity.getGameEntity())
        .userEntity(entity.getUserEntity())
        .build();
  }

  public static ParticipantGameEntity setWithdraw(ParticipantGameEntity entity) {
    return ParticipantGameEntity.builder()
        .id(entity.getId())
        .status(ParticipantGameStatus.WITHDRAW)
        .createdDateTime(entity.getCreatedDateTime())
        .acceptedDateTime(entity.getAcceptedDateTime())
        .withdrewDateTime(LocalDateTime.now())
        .gameEntity(entity.getGameEntity())
        .userEntity(entity.getUserEntity())
        .build();
  }

  public static ParticipantGameEntity gameCreatorInvite(InviteEntity inviteEntity) {

    LocalDateTime nowDateTime = LocalDateTime.now();

    return ParticipantGameEntity.builder()
            .status(ParticipantGameStatus.ACCEPT)
            .createdDateTime(nowDateTime)
            .acceptedDateTime(nowDateTime)
            .gameEntity(inviteEntity.getGameEntity())
            .userEntity(inviteEntity.getReceiverUserEntity())
            .build();
  }

  public static ParticipantGameEntity gameUserInvite(InviteEntity inviteEntity) {
    return ParticipantGameEntity.builder()
        .status(ParticipantGameStatus.APPLY)
        .gameEntity(inviteEntity.getGameEntity())
        .userEntity(inviteEntity.getReceiverUserEntity())
        .build();
  }

}
