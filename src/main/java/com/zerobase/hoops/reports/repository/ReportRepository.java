package com.zerobase.hoops.reports.repository;

import com.zerobase.hoops.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends
    JpaRepository<ReportEntity, Long> {

}