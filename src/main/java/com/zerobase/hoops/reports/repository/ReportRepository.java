package com.zerobase.hoops.reports.repository;

import com.zerobase.hoops.entity.ReportEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends
    JpaRepository<ReportEntity, Long> {

  Page<ReportEntity> findByBlackListStartDateTimeIsNull(
      Pageable pageable);

  boolean existsByUser_UserIdAndReportedUser_UserId(Long userId,
      Long userId1);

  Optional<ReportEntity> findByReportedUser_UserId(Long userId);

}
