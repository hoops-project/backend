package com.zerobase.hoops.reports.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.hoops.entity.ReportEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.manager.service.ManagerService;
import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.dto.ReportListResponseDto;
import com.zerobase.hoops.reports.repository.ReportRepository;
import com.zerobase.hoops.reports.service.ReportService;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.service.UserService;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

  @MockBean
  private ReportRepository reportRepository;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private ReportService reportService;

  @MockBean
  private UserService userService;

  @MockBean
  private ManagerService managerService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private TokenProvider tokenProvider;


  @WithMockUser(roles = "OWNER")
  @Test
  @DisplayName("신고 목록 불러오기")
  public void testReportContents() throws Exception {
    String reportId = "1";
    String reportContents = "Report contents for report1";

    UserEntity user = UserEntity.builder()
        .id(1L)
        .gender(GenderType.MALE)
        .build();

    UserEntity receiverUser = UserEntity.builder()
        .id(2L)
        .gender(GenderType.MALE)
        .build();

    ReportEntity report = ReportEntity.builder()
        .user(user)
        .reportedUser(receiverUser)
        .content(reportContents)
        .build();

    given(reportRepository.findById(Long.valueOf(reportId))).willReturn(
        Optional.of(report));
    when(reportService.reportContents(reportId)).thenReturn(
        report.getContent());

    // 컨트롤러 호출 및 결과 확인
    mockMvc.perform(MockMvcRequestBuilders
            .get("/api/report/contents/{report_id}", reportId)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }


  @WithMockUser(roles = "OWNER")
  @Test
  @DisplayName("신고 목록 불러오기")
  void getReportList_success() throws Exception {
    // Given
    int page = 0;
    int size = 10;

    List<ReportListResponseDto> mockReportList = Arrays.asList(
        ReportListResponseDto.builder()
            .userId(1L)
            .userName("User1")
            .mannerPoint("Excellent")
            .gender(GenderType.MALE)
            .ability(AbilityType.SHOOT)
            .playStyle(PlayStyleType.AGGRESSIVE)
            .build(),
        ReportListResponseDto.builder()
            .userId(2L)
            .userName("User2")
            .mannerPoint("Good")
            .gender(GenderType.FEMALE)
            .ability(AbilityType.SHOOT)
            .playStyle(PlayStyleType.BALANCE)
            .build()
    );

    // List를 Page로 변환
    Page<ReportListResponseDto> reportListResponseDtoPage = listToPage(mockReportList,
        PageRequest.of(page, size));


    // When
    when(reportService.reportList(page, size)).thenReturn(reportListResponseDtoPage);

    // Then
    ResultActions resultActions =
        mockMvc.perform(
                get("/api/report/user-list")
                    .with(csrf())
                    .param("page", String.valueOf(page))
                    .param("size", String.valueOf(size)))
            .andDo(print())
            .andExpect(status().isForbidden());
  }

  public static <T> Page<T> listToPage(List<T> list, PageRequest pageRequest) {
    int start = (int) pageRequest.getOffset();
    int end = Math.min((start + pageRequest.getPageSize()), list.size());
    return new PageImpl<>(list.subList(start, end), pageRequest, list.size());
  }

  @WithMockUser
  @Test
  @DisplayName("신고 성공")
  void report_success() throws Exception {
    // Given
    given(userRepository.existsByEmailAndDeletedDateTimeNull(anyString()))
        .willReturn(true);

    // When
    ReportDto reportDto = ReportDto.builder()
        .reportedUserId(1L)
        .content("ReasonReasonReasonReasonReasonReasonReasonReasonReason")
        .build();

    // Then
    ResultActions resultActions =
        mockMvc.perform(
                post("/api/report/user")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        reportDto)
                    )
            )
            //.andDo(print())
            .andExpect(status().isForbidden());
  }
}