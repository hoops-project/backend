package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.entity.MannerPointEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MannerPointRepository extends
    JpaRepository<MannerPointEntity, Long> {

   boolean existsByUser_UserIdAndReceiver_UserIdAndGame_GameId(
      Long userId, Long userId1, Long gameId);
}
