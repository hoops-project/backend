package com.zerobase.hoops.chat.repository;

import com.zerobase.hoops.entity.ChatRoomEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends
    JpaRepository<ChatRoomEntity, Long> {

  boolean existsByGameEntity_IdAndUserEntity_Id(Long gameId, Long userId);

  List<ChatRoomEntity> findByGameEntity_Id(Long id);

  Optional<ChatRoomEntity> findByGameEntity_IdAndUserEntity_Id(Long id, Long id1);

}
