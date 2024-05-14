package com.zerobase.hoops.invite.repository;

import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.invite.type.InviteStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends JpaRepository<InviteEntity, Long> {

  boolean existsByInviteStatusAndGameEntityGameIdAndReceiverUserEntityUserId(
      InviteStatus inviteStatus, Long gameId, Long receiverUserId);

  Optional<InviteEntity> findByInviteIdAndInviteStatus(Long inviteId, InviteStatus inviteStatus);

  List<InviteEntity> findByInviteStatusAndGameEntityGameId(InviteStatus inviteStatus, Long gameId);
  List<InviteEntity> findByInviteStatusAndSenderUserEntityUserIdOrReceiverUserEntityUserId(InviteStatus inviteStatus,
      Long SenderUserId, Long receiverUserId);

  List<InviteEntity> findByInviteStatusAndReceiverUserEntityUserId(InviteStatus inviteStatus, Long userId);
}
