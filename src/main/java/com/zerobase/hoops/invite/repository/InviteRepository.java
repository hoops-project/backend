package com.zerobase.hoops.invite.repository;

import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.invite.type.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends JpaRepository<InviteEntity, Long> {

  boolean existsByInviteStatusAndGameEntityGameIdAndReceiverUserEntityUserId(
      InviteStatus inviteStatus, Long gameId, Long receiverUserId);
}
