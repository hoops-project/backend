package com.zerobase.hoops.reports.repository;

import com.zerobase.hoops.document.ReportDocument;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ReportRepository extends
    ElasticsearchRepository<ReportDocument, String> {

  Page<ReportDocument> findByBlackListStartDateTimeIsNull(
      Pageable pageable);

  boolean existsByUser_IdAndReportedUser_Id(String userId,
      String userId1);

  Optional<ReportDocument> findByReportedUser_Id(String userId);

}
