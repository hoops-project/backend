package com.zerobase.hoops.friends.repository;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.friends.type.FriendStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

  Optional<FriendEntity> findByIdAndStatus(
      Long friendId, FriendStatus friendStatus);

  int countByUserEntityIdAndStatus(Long userId, FriendStatus friendStatus);

  Optional<FriendEntity> findByFriendUserEntityIdAndUserEntityIdAndStatus(
      Long friendUserId, Long userId, FriendStatus friendStatus);

  Page<FriendEntity> findByStatusAndUserEntityId(FriendStatus friendStatus,
      Long userId, Pageable pageable);

  boolean existsByUserEntityIdAndFriendUserEntityIdAndStatus(
      Long userId, Long receiverUserId, FriendStatus friendStatus);

  List<FriendEntity> findByUserEntityIdOrFriendUserEntityIdAndStatusNotAndDeletedDateTimeNull(
      Long userId, Long friendUserId, FriendStatus friendStatus);

  List<FriendEntity> findByStatusAndFriendUserEntityId(FriendStatus friendStatus, Long userId);

  boolean existsByUserEntityIdAndFriendUserEntityIdAndStatusIn(Long userId,
      Long friendUserId, List<FriendStatus> apply);
}
