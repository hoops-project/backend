package com.zerobase.hoops.reports.service;

import com.zerobase.hoops.alarm.domain.NotificationType;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    log.info("reportContents 시작");
    ReportEntity reportEntity = reportRepository.findById(
        Long.valueOf(reportId)).orElseThrow(
        () -> new CustomException(ErrorCode.NOT_EXIST_REPORTED)
    );
    log.info("reportContents 종료");
    return reportEntity.getContent();
  }

  public Page<ReportListResponseDto> reportList(int page, int size) {
    log.info("reportList 시작");
    Page<ReportEntity> reportPage = reportRepository.findByBlackListStartDateTimeIsNull(
        PageRequest.of(page, size));
    List<ReportListResponseDto> reportList = reportPage.getContent()
        .stream()
        .map(ReportListResponseDto::of)
        .toList();
    log.info("reportList 종료");
    return new PageImpl<>(reportList, reportPage.getPageable(),
        reportPage.getTotalElements());
  }

  public void reportUser(ReportDto request) {
    log.info("reportUser 시작");
    UserEntity user = userRepository.findById(
            jwtTokenExtract.currentUser().getId())
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    UserEntity reportedUser = userRepository.findById(
            request.getReportedUserId())
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));


    checkExist(request, user);

    notificationService.send(NotificationType.REPORT, findManger(),
        user.getNickName() + "에게서 신고가 접수되었습니다.");
    reportRepository.save(request.toEntity(user, reportedUser));
    log.info(
        String.format(
            "[user_login_id] = %s = / [reported_user_login_id] = %s 신고완료",
            user.getLoginId(),
            reportedUser.getLoginId()
        ));
    log.info("reportUser 종료");
  }

  private void checkExist(ReportDto request, UserEntity user) {
    boolean existReported = reportRepository.existsByUser_IdAndReportedUser_Id(
        user.getId(), request.getReportedUserId());
    if (existReported){
      throw new CustomException(ErrorCode.AlREADY_REPORTED);
    }

  }

  private UserEntity findManger() {
    return userRepository.findById(1L)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }
}
