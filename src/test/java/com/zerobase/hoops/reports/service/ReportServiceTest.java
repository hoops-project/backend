package com.zerobase.hoops.reports.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.ReportEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
//import com.zerobase.hoops.manager.service.ManagerService;
import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.repository.ReportRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;


@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReportRepository reportRepository;

  @InjectMocks
  private ReportService reportService;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  @MockBean
  //private ManagerService managerService;

  private UserEntity userEntity;
  private UserEntity reportedUserEntity;

  @BeforeEach
  void setUp() {
    userEntity = UserEntity.builder()
        .userId(1L)
        .id("user1")
        .password("password123")
        .email("user@example.com")
        .name("John Doe")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("johndoe")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(Collections.singletonList("ROLE_USER"))
        .createdDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
    reportedUserEntity = UserEntity.builder()
        .userId(2L)
        .id("user1")
        .password("password123")
        .email("reported@example.com")
        .name("John Doe")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("johndoe")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(Collections.singletonList("ROLE_USER"))
        .createdDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
  }

  @Test
  @DisplayName("신고하기 성공")
  void reportUser_validUsers_shouldSaveReport() {
    // Given
    ReportDto reportDto = ReportDto.builder()
        .reportedUserId(1L)
        .content("Reason")
        .build();

    when(jwtTokenExtract.currentUser()).thenReturn(userEntity);
    when(userRepository.findById(anyLong())).thenReturn(
        Optional.of(userEntity));
    when(userRepository.findById(anyLong())).thenReturn(
        Optional.of(reportedUserEntity));


    ArgumentCaptor<ReportEntity> reportEntityCaptor = ArgumentCaptor.forClass(
        ReportEntity.class);

    // When
    reportService.reportUser(reportDto);

    // Then
    verify(reportRepository).save(reportEntityCaptor.capture());
    ReportEntity savedReportEntity = reportEntityCaptor.getValue();
    assertThat(savedReportEntity.getContent()).isEqualTo(
        reportDto.getContent());
    assertThat(savedReportEntity.getUser()).isEqualTo(userEntity);
    assertThat(savedReportEntity.getReportedUser()).isEqualTo(
        reportedUserEntity);
  }

  @Test
  @DisplayName("신고하기 실패 - 존재하지 않는 유저")
  void reportUser_invalidReportedUser_shouldThrowException() {
    // Given
    ReportDto reportDto = ReportDto.builder()
        .reportedUserId(1L)
        .content("ReasonReasonReasonReasonReasonReason")
        .build();
    given(jwtTokenExtract.currentUser()).willReturn(userEntity);
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When, Then
    assertThrows(CustomException.class, () -> reportService.reportUser(reportDto));
  }

}