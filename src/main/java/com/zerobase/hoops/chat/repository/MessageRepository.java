package com.zerobase.hoops.chat.repository;

import com.zerobase.hoops.entity.ChatRoomEntity;
import com.zerobase.hoops.entity.MessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends
    JpaRepository<MessageEntity, Long> {

  List<MessageEntity> findByChatRoomEntity(ChatRoomEntity chatRoomEntity);

}
