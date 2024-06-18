package com.zerobase.hoops.manager.service;

import com.zerobase.hoops.entity.BlackListUserEntity;
import com.zerobase.hoops.entity.ReportEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.manager.dto.BlackListDto;
import com.zerobase.hoops.manager.dto.UnLockBlackListDto;
import com.zerobase.hoops.manager.repository.BlackListUserRepository;
import com.zerobase.hoops.reports.repository.ReportRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ManagerService {

  private final BlackListUserRepository blackListUserRepository;
  private final JwtTokenExtract jwtTokenExtract;
  private final UserRepository userRepository;
  private final ReportRepository reportRepository;

  public void getBlackList(String loginId) {
    log.info("getBlackList 시작");
    blackListUserRepository
        .findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(
            loginId, LocalDate.now())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_BLACKLIST));
    log.info("getBlackList 종료");
  }

  public void saveBlackList(BlackListDto request) {
    log.info("saveBlackList 시작");
    Long userId = jwtTokenExtract.currentUser().getId();
    Long reportedId = request.getReportedId();
    log.info(
        String.format("[user_pk] = %s = / [reported_pk] = %s",
            userId,
            reportedId
        ));
    startBlackListCheckFromReportEntity(request);
    validateBlackList(userId, reportedId);
    log.info("saveBlackList 종료");
  }

  private void startBlackListCheckFromReportEntity(BlackListDto request) {
    ReportEntity reportEntity = reportRepository.findByReportedUser_Id(
            request.getReportedId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    reportEntity.saveBlackListStartDateTime(LocalDateTime.now());
    reportRepository.save(reportEntity);
  }

  private void validateBlackList(Long userId, Long reportedId) {
    UserEntity userEntity = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    UserEntity reportedUserEntity = userRepository.findById(reportedId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    Optional<BlackListUserEntity> alreadyBlackUser =
        blackListUserRepository.findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(
            reportedUserEntity.getLoginId(), LocalDate.now());

    if (alreadyBlackUser.isEmpty()) {
      blackListUserRepository.save(BlackListUserEntity.builder()
          .blackUser(reportedUserEntity)
          .endDate(LocalDate.now().plusDays(10))
          .build());
    } else {
      throw new CustomException(ErrorCode.ALREADY_BLACKLIST);
    }
  }

  public void checkBlackList(String blackUserId) {
    log.info("checkBlackList 시작");
    Optional<BlackListUserEntity> blackUser = blackListUserRepository
        .findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(
            blackUserId, LocalDate.now());
    if (blackUser.isEmpty()) {
      log.info(
          String.format("[blackUser_pk] = %s / 데이터 없음",
              blackUserId
          ));
      return;
    }
    int comparison = blackUser.get().getEndDate()
        .compareTo(LocalDate.now());
    if (comparison > 0) {
      log.info(
          String.format("[blackUser_pk] = %s / 아직 BAN 기간 10일이 넘지 않음",
              blackUserId
          ));
      throw new CustomException(ErrorCode.BAN_FOR_10DAYS);
    }
    log.info("checkBlackList 종료");
  }

  public void unLockBlackList(UnLockBlackListDto request) {
    log.info("unLockBlackList 시작");
    BlackListUserEntity blackUser = blackListUserRepository
        .findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(
            request.getBlackUserId(), LocalDate.now())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_BLACKLIST));
    log.info("unLockBlackList 해제 시작");
    blackUser.unLockBlackList();
    blackListUserRepository.save(blackUser);
    log.info("unLockBlackList 해제 성공 및 저장");
    log.info("unLockBlackList 종료");
  }

}
