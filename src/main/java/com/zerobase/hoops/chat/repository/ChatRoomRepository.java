package com.zerobase.hoops.chat.repository;

import com.zerobase.hoops.entity.ChatRoomEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends
    JpaRepository<ChatRoomEntity, Long> {

  Optional<ChatRoomEntity> findByGameEntity_Id(Long gameId);
}
