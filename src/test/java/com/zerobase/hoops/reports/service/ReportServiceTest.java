package com.zerobase.hoops.reports.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.ReportEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.dto.ReportListResponseDto;
import com.zerobase.hoops.reports.repository.ReportRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


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

  @Mock
  private NotificationService notificationService;

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
  @DisplayName("신고내역 불러오기 성공")
  void testReportContents_ExistingReport() {
    // Given
    String reportId = "1";
    String reportContent = "This is a report content";
    ReportEntity reportEntity = ReportEntity.builder()
        .id(1L)
        .content(reportContent)
        .build();
    when(reportRepository.findById(anyLong())).thenReturn(
        Optional.of(reportEntity));

    // When
    String result = reportService.reportContents(reportId);

    // Then
    assertEquals(reportContent, result);
  }

  @Test
  @DisplayName("신고내역 불러오기 실패")
  void testReportContents_NonExistingReport() {
    // Given
    String reportId = "999";
    when(reportRepository.findById(anyLong())).thenReturn(
        Optional.empty());

    // When & Then
    assertThrows(CustomException.class,
        () -> reportService.reportContents(reportId));
  }

  @Test
  @DisplayName("유저 목록 불러오기")
  public void testReportList() {
    // Given
    ReportEntity reportEntity1 = ReportEntity.builder()
        .user(userEntity)
        .reportedUser(reportedUserEntity)
        .build();

    ReportEntity reportEntity2 = ReportEntity.builder()
        .user(reportedUserEntity)
        .reportedUser(userEntity)
        .build();

    List<ReportEntity> reportEntities = Arrays.asList(reportEntity1,
        reportEntity2);
    Page<ReportEntity> reportPage = new PageImpl<>(reportEntities);

    // When
    when(reportRepository.findByBlackListStartDateTimeIsNull(
        any(PageRequest.class)))
        .thenReturn(reportPage);
    List<ReportListResponseDto> result = reportService.reportList(0, 10);

    // Then
    verify(reportRepository).findByBlackListStartDateTimeIsNull(
        any(PageRequest.class));
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
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
    assertThrows(CustomException.class,
        () -> reportService.reportUser(reportDto));
  }

}