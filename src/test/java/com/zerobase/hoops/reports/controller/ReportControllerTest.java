package com.zerobase.hoops.reports.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.hoops.manager.service.ManagerService;
import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.repository.ReportRepository;
import com.zerobase.hoops.reports.service.ReportService;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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


  @WithMockUser
  @Test
  @DisplayName("신고 성공")
  void report_success() throws Exception {
    // Given
    given(userRepository.existsByEmail(anyString()))
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
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title")
                .value("유저신고"))
            .andExpect(jsonPath("$.detail")
                .value("Success"));
  }
}