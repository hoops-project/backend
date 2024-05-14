package com.zerobase.hoops.manager.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.BlackListUserEntity;
import com.zerobase.hoops.entity.ReportEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.manager.dto.BlackListDto;
import com.zerobase.hoops.manager.dto.UnLockBlackListDto;
import com.zerobase.hoops.manager.repository.BlackListUserRepository;
import com.zerobase.hoops.reports.repository.ReportRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

  @Mock
  private BlackListUserRepository blackListUserRepository;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReportRepository reportRepository;

  @InjectMocks
  private ManagerService managerService;


  @Test
  @DisplayName("블랙리스트 목록 성공 테스트")
  void getBlackListTest() {
    // Given
    String loginId = "testuser";
    LocalDate afterDate = LocalDate.now().plusDays(1);
    BlackListUserEntity blackListUserEntity = BlackListUserEntity.builder()
        .blackUser(new UserEntity())
        .endDate(afterDate)
        .build();
    blackListUserEntity.getBlackUser().setId(loginId);

    // When
    when(blackListUserRepository.findByBlackUser_IdAndEndDateAfter(loginId,
        LocalDate.now()))
        .thenReturn(Optional.of(blackListUserEntity));

    // Then
    assertDoesNotThrow(() -> managerService.getBlackList(loginId));
  }

  // saveBlackList 메서드 테스트
  @Test
  void saveBlackListTest() {
    BlackListDto request = new BlackListDto();
    request.setReportedId(1L);

    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(2L);

    UserEntity reportedUserEntity = new UserEntity();
    reportedUserEntity.setId("reportedUser");
    reportedUserEntity.setUserId(1L);

    ReportEntity reportEntity = ReportEntity.builder()
        .reportedUser(reportedUserEntity)
        .build();

    when(jwtTokenExtract.currentUser()).thenReturn(userEntity);
    when(userRepository.findById(2L)).thenReturn(Optional.of(userEntity));
    when(userRepository.findById(1L)).thenReturn(
        Optional.of(reportedUserEntity));
    when(blackListUserRepository.findByBlackUser_IdAndEndDateAfter(
        "reportedUser", LocalDate.now()))
        .thenReturn(Optional.empty());
    when(reportRepository.findByReportedUser_UserId(1L)).thenReturn(
        Optional.of(reportEntity));

    assertDoesNotThrow(() -> managerService.saveBlackList(request));
  }


  @Test
  @DisplayName("블랙리스트 체크 성공 테스트")
  void checkBlackListTest() {
    // Given
    String blackUserId = "testuser";
    LocalDate afterDate = LocalDate.now().plusDays(1);
    BlackListUserEntity blackListUserEntity = BlackListUserEntity.builder()
        .blackUser(new UserEntity())
        .endDate(afterDate)
        .build();
    blackListUserEntity.getBlackUser().setEmail(blackUserId);

    // When
    when(blackListUserRepository.findByBlackUser_EmailAndEndDateAfter(
        blackUserId, LocalDate.now()))
        .thenReturn(Optional.of(blackListUserEntity));

    // Then
    assertThrows(CustomException.class,
        () -> managerService.checkBlackList(blackUserId));
  }

  @Test
  @DisplayName("블랙리스트 해제 성공 테스트")
  void unLockBlackListTest() {
    // Given
    String blackUserId = "testuser";
    LocalDate afterDate = LocalDate.now().plusDays(1);
    BlackListUserEntity blackListUserEntity = BlackListUserEntity.builder()
        .blackUser(new UserEntity())
        .endDate(afterDate)
        .build();
    blackListUserEntity.getBlackUser().setId(blackUserId);

    UnLockBlackListDto request = new UnLockBlackListDto();
    request.setBlackUserId(blackUserId);

    // When
    when(blackListUserRepository.findByBlackUser_IdAndEndDateAfter(
        blackUserId, LocalDate.now()))
        .thenReturn(Optional.of(blackListUserEntity));

    // Then
    assertDoesNotThrow(() -> managerService.unLockBlackList(request));
  }
}