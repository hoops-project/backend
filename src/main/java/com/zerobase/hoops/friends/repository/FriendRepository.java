package com.zerobase.hoops.friends.repository;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.friends.type.FriendStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

  Optional<FriendEntity> findByFriendIdAndStatus(Long friendId, FriendStatus friendStatus);

  int findByFriendIdAndStatusIn(Long friendUserId, List<FriendStatus> apply);

  int findByFriendUserEntityUserIdAndStatus(Long friendUserId, FriendStatus friendStatus);

  int findByUserEntityUserIdAndStatus(Long userId, FriendStatus friendStatus);

  Optional<FriendEntity> findByStatusAndUserEntityUserIdAndFriendUseEntityUserId(Long friendUserId,
      Long userId, FriendStatus friendStatus);
}
