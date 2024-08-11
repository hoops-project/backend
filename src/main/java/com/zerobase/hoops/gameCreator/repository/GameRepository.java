package com.zerobase.hoops.gameCreator.repository;

import com.zerobase.hoops.document.GameDocument;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends
    ElasticsearchRepository<GameDocument, String> {

  Optional<GameDocument> findByIdAndDeletedDateTimeNull(String gameId);

  List<GameDocument> findByUserIdAndDeletedDateTimeNull(String userId);

  boolean existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull(
      OffsetDateTime beforeDatetime,
      OffsetDateTime afterDateTime, String address);

  boolean existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndIdNot(
      OffsetDateTime beforeDatetime, OffsetDateTime afterDateTime,
      String address, String gameId);

}


