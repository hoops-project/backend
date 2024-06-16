package com.zerobase.hoops.invite.repository;

import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.invite.type.InviteStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends JpaRepository<InviteEntity, Long> {

  boolean existsByInviteStatusAndGameIdAndReceiverUserId(
      InviteStatus inviteStatus, Long gameId, Long receiverUserId);

  Optional<InviteEntity> findByIdAndInviteStatus(Long inviteId, InviteStatus inviteStatus);

  List<InviteEntity> findByInviteStatusAndGameId(InviteStatus inviteStatus, Long gameId);
  List<InviteEntity> findByInviteStatusAndSenderUserIdOrReceiverUserId(InviteStatus inviteStatus,
      Long SenderUserId, Long receiverUserId);

  Page<InviteEntity> findByInviteStatusAndReceiverUserId(InviteStatus inviteStatus, Long userId, Pageable pageable);
}
