package com.zerobase.hoops.alarm.repository;

import com.zerobase.hoops.document.NotificationDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends
    ElasticsearchRepository<NotificationDocument, String> {
  List<NotificationDocument> findAllByReceiverIdOrderByCreatedDateTimeDesc(String userId);
}
