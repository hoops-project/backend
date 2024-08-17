package com.zerobase.hoops.reports.repository;

import com.zerobase.hoops.entity.ReportEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends
    JpaRepository<ReportEntity, Long> {

  Page<ReportEntity> findByBlackListStartDateTimeIsNull(
      Pageable pageable);

  boolean existsByUser_IdAndReportedUser_Id(Long userId,
      Long userId1);

  Optional<List<ReportEntity>> findByReportedUser_Id(Long id);

}
