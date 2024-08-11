package com.zerobase.hoops.friends.repository;

import com.zerobase.hoops.document.FriendDocument;
import com.zerobase.hoops.friends.type.FriendStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends
    ElasticsearchRepository<FriendDocument, String> {

  Optional<FriendDocument> findByIdAndStatus(
      String friendId, FriendStatus friendStatus);

  int countByUserIdAndStatus(String userId, FriendStatus friendStatus);

  Optional<FriendDocument> findByFriendUserIdAndUserIdAndStatus(
      String friendUserId, String userId, FriendStatus friendStatus);

  Page<FriendDocument> findByStatusAndUserId(FriendStatus friendStatus,
      String userId, Pageable pageable);

  boolean existsByUserIdAndFriendUserIdAndStatus(
      String userId, String receiverUserId, FriendStatus friendStatus);

  List<FriendDocument> findByUserIdOrFriendUserIdAndStatusNotAndDeletedDateTimeNull(
      String userId, String friendUserId, FriendStatus friendStatus);

  Page<FriendDocument> findByStatusAndFriendUserId(FriendStatus friendStatus,
      String userId, Pageable pageable);

  boolean existsByUserIdAndFriendUserIdAndStatusIn(String userId,
      String friendUserId, List<FriendStatus> apply);
}
