package com.zerobase.hoops.alarm.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.hoops.alarm.domain.NotificationDto;
import com.zerobase.hoops.alarm.domain.NotificationType;
import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.manager.service.ManagerService;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.service.UserService;
import com.zerobase.hoops.users.type.GenderType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

  @InjectMocks
  private NotificationController notificationController;
  @Mock
  private NotificationService notificationService;

  @MockBean
  private TokenProvider tokenProvider;

  @MockBean
  private UserService userService;

  @MockBean
  private ManagerService managerService;


  private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void initMockMvc() {
    mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
  }

  @DisplayName("SSE 연결")
  @Test
  void subscribeTest() throws Exception {

    UserEntity user = UserEntity.builder()
        .userId(2L)
        .roles(Collections.singletonList("OWNER"))
        .gender(GenderType.MALE)
        .build();

    given(notificationService.subscribe(any(), anyString())).willReturn(
        new SseEmitter());

    // when
    ResultActions result = this.mockMvc.perform(
        get("/subscribe")
            .with(user(user))
            .accept(MediaType.TEXT_EVENT_STREAM_VALUE)
            .header("lastEventId", "1_1631593143664")
    );

    // then
    result.andExpect(status().isOk());
  }

  @Test
  @DisplayName("로그인 한 사용자의 모든 알림 조회")
  @WithMockUser(roles = "OWNER")
  void notificationsTest() throws Exception {
    // given
    List<NotificationDto> notificationDtos = new ArrayList<>();
    notificationDtos.add(
        new NotificationDto(1L, "FRIEND",
            "첫번째알림", LocalDateTime.now().plusDays(1)));
    notificationDtos.add(
        new NotificationDto(2L,"REPORT",
            "두번째알림", LocalDateTime.now().plusDays(2)));
    notificationDtos.add(
        new NotificationDto(3L, "REJECTED_GAME",
            "세번째알림", LocalDateTime.now().plusDays(3)));

    // when
    when(notificationService.findAllById(any())).thenReturn(notificationDtos);

    // then
    ResultActions result =
        mockMvc.perform(
                get("/notifications")
                    .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(1L));
  }
}
