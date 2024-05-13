package com.zerobase.hoops.reports.service;

import com.zerobase.hoops.entity.ReportEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.dto.ReportListResponse;
import com.zerobase.hoops.reports.repository.ReportRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ReportService {

  public final ReportRepository reportRepository;
  public final UserRepository userRepository;
  private final JwtTokenExtract jwtTokenExtract;

  public void reportUser(ReportDto request) {

    UserEntity user = userRepository.findById(
            jwtTokenExtract.currentUser().getUserId())
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    UserEntity reportedUser = userRepository.findById(
            request.getReportedUserId())
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    checkExist(request, user);

    reportRepository.save(request.toEntity(user, reportedUser));
  }

  private void checkExist(ReportDto request, UserEntity user) {
    boolean existReported = reportRepository.existsByUser_UserIdAndReportedUser_UserId(
        user.getUserId(), request.getReportedUserId());
    if (existReported) {
      throw new CustomException(ErrorCode.AlREADY_REPORTED);
    }
  }


}
