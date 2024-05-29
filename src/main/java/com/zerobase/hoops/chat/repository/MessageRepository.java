package com.zerobase.hoops.chat.repository;

import com.zerobase.hoops.entity.MessageEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends
    JpaRepository<MessageEntity, Long> {

  Optional<List<MessageEntity>> findByChatRoomEntity_RoomIdAndSessionId(
      Long roomId, Long sessionId);

}
