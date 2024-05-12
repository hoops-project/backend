package com.zerobase.hoops.reports.repository;

import com.zerobase.hoops.entity.ReportEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends
    JpaRepository<ReportEntity, Long> {

  boolean existsByUser_UserIdAndReportedUser_UserId(Long userId,
      Long userId1);
}
