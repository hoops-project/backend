package com.zerobase.hoops.chat.repository;

import com.zerobase.hoops.document.ChatRoomDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends
    ElasticsearchRepository<ChatRoomDocument, String> {

  List<ChatRoomDocument> findByGameDocument_Id(String gameId);

  boolean existsByGameDocument_IdAndUserDocument_Id(String id, String id1);

  Optional<ChatRoomDocument> findByGameDocument_IdAndUserDocument_Id(String gameIdNumber, String id);
}
