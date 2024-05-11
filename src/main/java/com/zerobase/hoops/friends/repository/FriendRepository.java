package com.zerobase.hoops.friends.repository;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.dto.FriendDto.SearchResponse;
import com.zerobase.hoops.friends.type.FriendStatus;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

  Optional<FriendEntity> findByFriendIdAndStatus(Long friendId, FriendStatus friendStatus);

  int countByUserEntityUserIdAndStatus(Long userId, FriendStatus friendStatus);

  Optional<FriendEntity> findByFriendUserEntityUserIdAndUserEntityUserIdAndStatus(Long friendUserId,
      Long userId, FriendStatus friendStatus);

  int countByFriendUserEntityUserIdAndStatusIn(Long friendUserId, List<FriendStatus> apply);

  Page<FriendEntity> findByUserEntityUserId(Long userId, Pageable pageable);
}
