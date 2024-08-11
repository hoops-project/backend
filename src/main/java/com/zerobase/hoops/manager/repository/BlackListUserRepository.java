package com.zerobase.hoops.manager.repository;

import com.zerobase.hoops.document.BlackListUserDocument;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListUserRepository extends
    ElasticsearchRepository<BlackListUserDocument, String> {

  Optional<BlackListUserDocument> findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(
      String id, LocalDate currentDate);

}
