package com.zerobase.hoops.manager.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.hoops.commonResponse.ApiResponseFactory;
import com.zerobase.hoops.manager.dto.BlackListDto;
import com.zerobase.hoops.manager.dto.UnLockBlackListDto;
import com.zerobase.hoops.manager.repository.BlackListUserRepository;
import com.zerobase.hoops.manager.service.ManagerService;
import com.zerobase.hoops.reports.repository.ReportRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ManagerService managerService;

  @MockBean
  private TokenProvider tokenProvider;

  @MockBean
  private UserService userService;

  @MockBean
  private ApiResponseFactory apiResponseFactory;

  @MockBean
  private ReportRepository reportRepository;

  @MockBean
  private JwtTokenExtract jwtTokenExtract;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private BlackListUserRepository blackListUserRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @WithMockUser(roles = "OWNER")
  @Test
  void saveBlackListTest() throws Exception {
    BlackListDto request = new BlackListDto();
    request.setReportedId(1L);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/manager/black-list")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser(roles = "OWNER")
  @Test
  void getBlackListTest() throws Exception {
    String loginId = "testuser";

    mockMvc.perform(MockMvcRequestBuilders.get("/api/manager/black-list")
            .with(csrf())
            .param("loginId", loginId))
        .andExpect(status().isForbidden());
  }

  @WithMockUser(roles = "OWNER")
  @Test
  void unLockBlackListTest() throws Exception {
    UnLockBlackListDto request = new UnLockBlackListDto();
    request.setBlackUserId("testuser");

    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/manager/unlock-black-list")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

}