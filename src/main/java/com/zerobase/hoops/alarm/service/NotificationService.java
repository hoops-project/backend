package com.zerobase.hoops.alarm.service;


import com.zerobase.hoops.alarm.domain.NotificationType;
import com.zerobase.hoops.entity.NotificationEntity;
import com.zerobase.hoops.alarm.domain.NotificationDto;
import com.zerobase.hoops.alarm.repository.EmitterRepository;
import com.zerobase.hoops.alarm.repository.NotificationRepository;
import com.zerobase.hoops.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@Slf4j
public class NotificationService {

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

  private final EmitterRepository emitterRepository;
  private final NotificationRepository notificationRepository;

  public NotificationService(EmitterRepository emitterRepository,
      NotificationRepository notificationRepository) {
    this.emitterRepository = emitterRepository;
    this.notificationRepository = notificationRepository;
  }

  /**
   * 사용자가 subscribe 요청하는 메서드, 즉 SSE 세션 연결하는 메서드
   *
   * @param user
   * @return
   */
  public SseEmitter subscribe(UserEntity user, String lastEventId) {

    String emitterId = user.getId() + "_" + System.currentTimeMillis();

    SseEmitter emitter = emitterRepository.save(emitterId,
        new SseEmitter(DEFAULT_TIMEOUT));

    // 상황별 emitter 연결 종료 처리
    emitter.onCompletion(
        () -> emitterRepository.deleteByEmitterId(emitterId));
    emitter.onTimeout(() -> emitterRepository.deleteByEmitterId(emitterId));
    emitter.onError((e) -> emitterRepository.deleteByEmitterId(emitterId));

    // 503 Service Unavailable 방지용 dummy event 전송
    sendToClient(emitter, emitterId,
        "EventStream Created. [emitterId=" + emitterId + "]");

    //클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실 예방
    if (!lastEventId.isEmpty()) {
      Map<String, Object> events =
          emitterRepository.findAllEventCacheStartWithUserId(
              String.valueOf(user.getId()));
      events.entrySet().stream()
          .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
          .forEach(
              entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
    }

    return emitter;
  }


  private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
    try {
      emitter.send(SseEmitter.event()
          .id(emitterId)
          .name("sse")
          .data(data));
    } catch (IOException e) {
      emitterRepository.deleteByEmitterId(emitterId);
      log.error("SSE 연결 오류!", e);
    }
  }

  @Transactional
  public void send(NotificationType type, UserEntity receiver, String content) {
    NotificationEntity notificationEntity = createNotification(
        type, receiver, content);
    notificationRepository.save(notificationEntity);
    Map<String, SseEmitter> sseEmitters =
        emitterRepository.findAllStartWithByUserId(
            String.valueOf(receiver.getId())
        );
    sseEmitters.forEach(
        (key, emitter) -> {
          emitterRepository.saveEventCache(key, notificationEntity);
          sendToClient(emitter, key, NotificationDto.entityToDto(
              notificationEntity));
        }
    );
  }

  private NotificationEntity createNotification(NotificationType type,
      UserEntity receiver, String content) {
    return NotificationEntity.builder()
        .receiver(receiver)
        .notificationType(type)
        .content(content)
        .createdDateTime(LocalDateTime.now())
        .build();
  }


  public List<NotificationDto> findAllById(UserEntity loginUser) {
    List<NotificationDto> responses =
        notificationRepository
            .findAllByReceiverIdOrderByCreatedDateTimeDesc(loginUser.getId())
            .stream().map(NotificationDto::entityToDto)
            .collect(Collectors.toList());

    return responses;
  }

}
