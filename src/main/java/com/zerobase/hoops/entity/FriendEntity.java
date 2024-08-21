package com.zerobase.hoops.entity;

import com.zerobase.hoops.friends.type.FriendStatus;
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

@Entity(name = "friend")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class FriendEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FriendStatus status;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdDateTime;

  private LocalDateTime acceptedDateTime;

  private LocalDateTime rejectedDateTime;

  private LocalDateTime canceledDateTime;

  private LocalDateTime deletedDateTime;

  @ManyToOne
  @JoinColumn(nullable = false)
  private UserEntity user;

  @ManyToOne
  @JoinColumn(nullable = false)
  private UserEntity friendUser;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FriendEntity that = (FriendEntity) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(status, that.status) &&
        Objects.equals(createdDateTime, that.createdDateTime) &&
        Objects.equals(acceptedDateTime, that.acceptedDateTime) &&
        Objects.equals(rejectedDateTime, that.rejectedDateTime) &&
        Objects.equals(canceledDateTime, that.canceledDateTime) &&
        Objects.equals(deletedDateTime, that.deletedDateTime) &&
        Objects.equals(user, that.user) &&
        Objects.equals(friendUser, that.friendUser);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, status, createdDateTime, acceptedDateTime,
        rejectedDateTime, canceledDateTime, deletedDateTime, user, friendUser);
  }

  public static FriendEntity setCancel(FriendEntity friendEntity,
      Clock clock) {
    return FriendEntity.builder()
        .id(friendEntity.getId())
        .status(FriendStatus.CANCEL)
        .createdDateTime(friendEntity.getCreatedDateTime())
        .canceledDateTime(LocalDateTime.now(clock))
        .user(friendEntity.getUser())
        .friendUser(friendEntity.getFriendUser())
        .build();
  }

  public static FriendEntity setAcceptMyFriend(FriendEntity friendEntity,
      Clock clock) {
    return FriendEntity.builder()
        .id(friendEntity.getId())
        .status(FriendStatus.ACCEPT)
        .createdDateTime(friendEntity.getCreatedDateTime())
        .acceptedDateTime(LocalDateTime.now(clock))
        .user(friendEntity.getUser())
        .friendUser(friendEntity.getFriendUser())
        .build();
  }

  public static FriendEntity setAcceptOtherFriend(FriendEntity friendEntity) {
    return FriendEntity.builder()
        .status(FriendStatus.ACCEPT)
        .createdDateTime(friendEntity.getCreatedDateTime())
        .acceptedDateTime(friendEntity.getAcceptedDateTime())
        .user(friendEntity.getFriendUser())
        .friendUser(friendEntity.getUser())
        .build();
  }

  public static FriendEntity setReject(FriendEntity friendEntity,
      Clock clock) {
    return FriendEntity.builder()
        .id(friendEntity.getId())
        .status(FriendStatus.REJECT)
        .createdDateTime(friendEntity.getCreatedDateTime())
        .rejectedDateTime(LocalDateTime.now(clock))
        .user(friendEntity.getUser())
        .friendUser(friendEntity.getFriendUser())
        .build();
  }

  public static FriendEntity setDeleteMyFriend(FriendEntity friendEntity,
      Clock clock) {
    return FriendEntity.builder()
        .id(friendEntity.getId())
        .status(FriendStatus.DELETE)
        .createdDateTime(friendEntity.getCreatedDateTime())
        .acceptedDateTime(friendEntity.getAcceptedDateTime())
        .deletedDateTime(LocalDateTime.now(clock))
        .user(friendEntity.getUser())
        .friendUser(friendEntity.getFriendUser())
        .build();
  }

  public static FriendEntity setDeleteOtherFriend(FriendEntity selfFriendEntity,
      FriendEntity otherFriendEntity) {
    return FriendEntity.builder()
        .id(otherFriendEntity.getId())
        .status(FriendStatus.DELETE)
        .createdDateTime(otherFriendEntity.getCreatedDateTime())
        .acceptedDateTime(otherFriendEntity.getAcceptedDateTime())
        .deletedDateTime(selfFriendEntity.getDeletedDateTime())
        .user(otherFriendEntity.getUser())
        .friendUser(otherFriendEntity.getFriendUser())
        .build();
  }

}
