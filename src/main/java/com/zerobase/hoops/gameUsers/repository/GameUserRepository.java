package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.document.GameDocument;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface GameUserRepository extends
    ElasticsearchRepository<GameDocument, String> {

  List<GameDocument> findByAddressContainingIgnoreCaseAndStartDateTimeAfterOrderByStartDateTimeAsc(
      String partOfAddress, OffsetDateTime currentDateTime);

  Optional<GameDocument> findByIdAndStartDateTimeBefore(String gameId,
      OffsetDateTime dateTime);
}
