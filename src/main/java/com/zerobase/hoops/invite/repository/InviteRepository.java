package com.zerobase.hoops.invite.repository;

import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.invite.type.InviteStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends JpaRepository<InviteEntity, Long> {

  boolean existsByInviteStatusAndGameEntityIdAndReceiverUserEntityId(
      InviteStatus inviteStatus, Long gameId, Long receiverUserId);

  Optional<InviteEntity> findByIdAndInviteStatus(Long inviteId, InviteStatus inviteStatus);

  List<InviteEntity> findByInviteStatusAndGameEntityId(InviteStatus inviteStatus, Long gameId);
  List<InviteEntity> findByInviteStatusAndSenderUserEntityIdOrReceiverUserEntityId(InviteStatus inviteStatus,
      Long SenderUserId, Long receiverUserId);

  List<InviteEntity> findByInviteStatusAndReceiverUserEntityId(InviteStatus inviteStatus, Long userId);
}
