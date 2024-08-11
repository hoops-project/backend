package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.document.MannerPointDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MannerPointRepository extends
    ElasticsearchRepository<MannerPointDocument, String> {

   boolean existsByUser_IdAndReceiver_IdAndGame_Id(
       String userId, String receiverId, String gameId);
}
