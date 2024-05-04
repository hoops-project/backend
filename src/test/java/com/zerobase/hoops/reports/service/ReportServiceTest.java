package com.zerobase.hoops.reports.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.ReportEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.repository.ReportRepository;
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


@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReportRepository reportRepository;

  @InjectMocks
  private ReportService reportService;


  private UserEntity userEntity;
  private UserEntity reportedUserEntity;

  @BeforeEach
  void setUp() {
    userEntity = UserEntity.builder()
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
        .createDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
    reportedUserEntity = UserEntity.builder()
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
        .createDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
  }

  @Test
  @DisplayName("신고하기 성공")
  void reportUser_validUsers_shouldSaveReport() {
    // Given
    ReportDto reportDto = ReportDto.builder()
        .userEmail("user@example.com")
        .reportedUserEmail("reported@example.com")
        // content의 최소 및 최대 글자 제한 수 유효성 검사는 Controller 에서 진행
        .content(
            "Reason")
        .build();

    when(userRepository.findByEmail("user@example.com")).thenReturn(
        Optional.of(userEntity));
    when(userRepository.findByEmail("reported@example.com")).thenReturn(
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
    assertThat(savedReportEntity.getUserId()).isEqualTo(
        userEntity.getEmail());
    assertThat(savedReportEntity.getReportedId()).isEqualTo(
        reportedUserEntity.getEmail());
  }

  @Test
  @DisplayName("신고하기 실패 - 존재하지 않는 유저")
  void reportUser_invalidReportedUser_shouldThrowException() {
    // Given
    ReportDto reportDto = ReportDto.builder()
        .userEmail("user@example.com")
        .reportedUserEmail("reported@example.com")
        .content("Reason")
        .build();
    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEntity));
    when(userRepository.findByEmail("reported@example.com")).thenReturn(Optional.empty());

    // When, Then
    assertThrows(CustomException.class, () -> reportService.reportUser(reportDto));
  }

}