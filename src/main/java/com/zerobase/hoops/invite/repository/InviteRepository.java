package com.zerobase.hoops.invite.repository;

import com.zerobase.hoops.document.InviteDocument;
import com.zerobase.hoops.invite.type.InviteStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends
    ElasticsearchRepository<InviteDocument, String> {

  boolean existsByInviteStatusAndGameIdAndReceiverUserId(
      InviteStatus inviteStatus, String gameId, String receiverUserId);

  Optional<InviteDocument> findByIdAndInviteStatus(String inviteId, InviteStatus inviteStatus);

  List<InviteDocument> findByInviteStatusAndGameId(InviteStatus inviteStatus, String gameId);
  List<InviteDocument> findByInviteStatusAndSenderUserIdOrReceiverUserId(InviteStatus inviteStatus,
      String SenderUserId, String receiverUserId);

  Page<InviteDocument> findByInviteStatusAndReceiverUserId(InviteStatus inviteStatus, String userId, Pageable pageable);
}
