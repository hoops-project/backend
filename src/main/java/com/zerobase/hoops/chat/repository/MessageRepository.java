package com.zerobase.hoops.chat.repository;

import com.zerobase.hoops.document.ChatRoomDocument;
import com.zerobase.hoops.document.MessageDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends
    ElasticsearchRepository<MessageDocument, String> {

  List<MessageDocument> findByChatRoom(ChatRoomDocument chatRoom);
}
