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

  public void getBlackList(String loginId){
    blackListUserRepository
        .findByBlackUser_IdAndEndDateAfter(
            loginId, LocalDate.now())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_BLACKLIST));
  }

  public void saveBlackList(BlackListDto request) {
    Long userId = jwtTokenExtract.currentUser().getUserId();
    Long reportedId = request.getReportedId();

    startBlackListCheckFromReportEntity(request);
    validateBlackList(userId, reportedId);
  }

  private void startBlackListCheckFromReportEntity(BlackListDto request) {
    ReportEntity reportEntity = reportRepository.findByReportedUser_UserId(
            request.getReportedId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    reportEntity.saveBlackListStartDateTime(LocalDateTime.now());
  }

  private void validateBlackList(Long userId, Long reportedId) {
    UserEntity userEntity = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    UserEntity reportedUserEntity = userRepository.findById(reportedId)
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    Optional<BlackListUserEntity> alreadyBlackUser =
        blackListUserRepository.findByBlackUser_IdAndEndDateAfter(
            reportedUserEntity.getId(), LocalDate.now());

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
    Optional<BlackListUserEntity> blackUser = blackListUserRepository
        .findByBlackUser_EmailAndEndDateAfter(blackUserId,
            LocalDate.now());
    Optional<BlackListUserEntity> blackUser2 = blackListUserRepository
        .findByBlackUser_EmailAndEndDateAfter(blackUserId,
            LocalDate.now());
    if (blackUser.isEmpty()) {
      return;
    }
    int comparison = blackUser.get().getEndDate()
        .compareTo(LocalDate.now());
    if (comparison > 0) {
      throw new CustomException(ErrorCode.BAN_FOR_10DAYS);
    }
  }

  public void unLockBlackList(UnLockBlackListDto request) {
    BlackListUserEntity blackUser = blackListUserRepository
        .findByBlackUser_IdAndEndDateAfter(
            request.getBlackUserId(), LocalDate.now())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_BLACKLIST));
    blackUser.unLockBlackList();
    blackListUserRepository.save(blackUser);
  }

}
