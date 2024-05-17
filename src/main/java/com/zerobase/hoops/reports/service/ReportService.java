package com.zerobase.hoops.reports.service;

import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.ReportEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.dto.ReportListResponseDto;
import com.zerobase.hoops.reports.repository.ReportRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

  public final ReportRepository reportRepository;
  public final UserRepository userRepository;
  private final JwtTokenExtract jwtTokenExtract;
  private final NotificationService notificationService;

  public String reportContents(String reportId) {
    ReportEntity reportEntity = reportRepository.findById(
        Long.valueOf(reportId)).orElseThrow(
        () -> new CustomException(ErrorCode.NOT_EXIST_REPORTED)
    );
    return reportEntity.getContent();
  }

  public List<ReportListResponseDto> reportList(int page, int size) {
    Page<ReportEntity> reportPage = reportRepository.findByBlackListStartDateTimeIsNull(
        PageRequest.of(page, size));
    List<ReportEntity> reportList = reportPage.getContent();

    return reportList.stream().map(ReportListResponseDto::of)
        .collect(Collectors.toList());
  }

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

    notificationService.send(findManger(), "신고가 접수되었습니다.");

    reportRepository.save(request.toEntity(user, reportedUser));
  }

  private void checkExist(ReportDto request, UserEntity user) {
    boolean existReported = reportRepository.existsByUser_UserIdAndReportedUser_UserId(
        user.getUserId(), request.getReportedUserId());
  }

  private UserEntity findManger() {
    return userRepository.findById(1L)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }
}
