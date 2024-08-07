package com.zerobase.hoops.alarm.controller;

import com.zerobase.hoops.alarm.domain.NotificationDto;
import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

  private final NotificationService notificationService;


  @Operation(summary = "SSE 세션 연결")
  @GetMapping(value = "/subscribe",
      produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public ResponseEntity<SseEmitter> subscribe(
      @AuthenticationPrincipal UserEntity user,
      @RequestHeader(value = "lastEventId", required = false, defaultValue = "")
      String lastEventId
  ) {
    SseEmitter sseEmitter = notificationService.subscribe(user,
        lastEventId);

    return ResponseEntity.ok(sseEmitter);
  }


  @Operation(summary = "해당 사용자에게 온 알림 전체 조회")
  @GetMapping("/notifications")
  public ResponseEntity<List<NotificationDto>> notifications(
      @AuthenticationPrincipal UserEntity user
  ) {
    return ResponseEntity.ok().body(notificationService.findAllById(user));
  }

}
