package com.zerobase.hoops.manager.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.document.BlackListUserDocument;
import com.zerobase.hoops.document.ReportDocument;
import com.zerobase.hoops.document.UserDocument;
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
    BlackListUserDocument blackListUserDocument = BlackListUserDocument.builder()
        .blackUser(new UserDocument())
        .endDate(afterDate)
        .build();
    blackListUserDocument.getBlackUser().setLoginId(loginId);

    // When
    when(blackListUserRepository.findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(loginId,
        LocalDate.now()))
        .thenReturn(Optional.of(blackListUserDocument));

    // Then
    assertDoesNotThrow(() -> managerService.getBlackList(loginId));
  }

  @Test
  @DisplayName("블랙리스트 목록 실패 테스트")
  void getBlackList_NotBlackList_ThrowsException() {
    // Given
    String loginId = "testUser";

    // When
    when(blackListUserRepository.findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(loginId,
        LocalDate.now()))
        .thenReturn(Optional.empty());

    // Then
    assertThrows(CustomException.class,
        () -> managerService.getBlackList(loginId));
  }

  @Test
  @DisplayName("유저 블랙리스트 저장 성공 테스트")
  void saveBlackListTest() {
    // Given
    BlackListDto request = new BlackListDto();
    request.setReportedId("1");

    UserDocument userDocument = new UserDocument();
    userDocument.setId("2");

    UserDocument reportedUserDocument = new UserDocument();
    reportedUserDocument.setLoginId("reportedUser");
    reportedUserDocument.setId("1");

    ReportDocument reportDocument = ReportDocument.builder()
        .reportedUser(reportedUserDocument)
        .build();

    // Then
    when(jwtTokenExtract.currentUser()).thenReturn(userDocument);
    when(userRepository.findById("2")).thenReturn(Optional.of(userDocument));
    when(userRepository.findById("1")).thenReturn(
        Optional.of(reportedUserDocument));
    when(blackListUserRepository.findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(
        "reportedUser", LocalDate.now()))
        .thenReturn(Optional.empty());
    when(reportRepository.findByReportedUser_Id("1")).thenReturn(
        Optional.of(reportDocument));

    // When
    assertDoesNotThrow(() -> managerService.saveBlackList(request));
  }

  @Test
  @DisplayName("유저 블랙리스트 저장 실패 테스트 - 이미 블랙리스트인 경우")
  void validateBlackList_AlreadyBlacklisted_ThrowsException() {
    // Given
    LocalDate afterDate = LocalDate.now().plusDays(1);

    BlackListDto request = new BlackListDto();
    request.setReportedId("1");

    UserDocument userDocument = new UserDocument();
    userDocument.setId("2");

    UserDocument reportedUserDocument = new UserDocument();
    reportedUserDocument.setLoginId("reportedUser");
    reportedUserDocument.setId("1");

    BlackListUserDocument blackListUserDocument = BlackListUserDocument.builder()
        .blackUser(reportedUserDocument)
        .endDate(afterDate)
        .build();
    blackListUserDocument.getBlackUser().setId(reportedUserDocument.getId());

    // When
    when(jwtTokenExtract.currentUser()).thenReturn(userDocument);

    // Then
    assertThrows(CustomException.class,
        () -> managerService.saveBlackList(request));
  }

  @Test
  @DisplayName("블랙리스트 체크 성공 테스트")
  void checkBlackListTest() {
    // Given
    String blackUserId = "testuser";
    LocalDate afterDate = LocalDate.now().plusDays(1);
    BlackListUserDocument blackListUserDocument = BlackListUserDocument.builder()
        .blackUser(new UserDocument())
        .endDate(afterDate)
        .build();
    blackListUserDocument.getBlackUser().setEmail(blackUserId);

    // When
    when(
        blackListUserRepository.findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(
            blackUserId, LocalDate.now()))
        .thenReturn(Optional.of(blackListUserDocument));

    // Then
    assertThrows(CustomException.class,
        () -> managerService.checkBlackList(blackUserId));
  }

  @Test
  @DisplayName("블랙리스트 체크 실패 테스트")
  void checkBlackList_NotBlacklisted_DoesNotThrowException() {
    // given
    String blackUserId = "testUser";

    //// When
    when(
        blackListUserRepository.findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(
            blackUserId, LocalDate.now()))
        .thenReturn(Optional.empty());
    managerService.checkBlackList(blackUserId);

    // Then
    assertDoesNotThrow(() -> managerService.checkBlackList(blackUserId));
  }

  @Test
  @DisplayName("블랙리스트 해제 성공 테스트")
  void unLockBlackListTest() {
    // Given
    String blackUserId = "testuser";
    LocalDate afterDate = LocalDate.now().plusDays(1);
    BlackListUserDocument blackListUserDocument = BlackListUserDocument.builder()
        .blackUser(new UserDocument())
        .endDate(afterDate)
        .build();
    blackListUserDocument.getBlackUser().setLoginId(blackUserId);

    UnLockBlackListDto request = new UnLockBlackListDto();
    request.setBlackUserId(blackUserId);

    // When
    when(blackListUserRepository.findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(
        blackUserId, LocalDate.now()))
        .thenReturn(Optional.of(blackListUserDocument));

    // Then
    assertDoesNotThrow(() -> managerService.unLockBlackList(request));
  }

  @Test
  @DisplayName("블랙리스트 해제 실패 테스트")
  void unLockBlackList_NotBlacklisted_ThrowsException() {
    // Given
    String blackUser = "123";
    UnLockBlackListDto unLockBlackListDto = new UnLockBlackListDto();
    unLockBlackListDto.setBlackUserId(blackUser);

    // When
    when(blackListUserRepository.findByBlackUser_loginIdAndBlackUser_DeletedDateTimeNullAndEndDateAfter(
        blackUser, LocalDate.now()))
        .thenReturn(Optional.empty());

    // Then
    assertThrows(CustomException.class,
        () -> managerService.unLockBlackList(unLockBlackListDto));
  }
}