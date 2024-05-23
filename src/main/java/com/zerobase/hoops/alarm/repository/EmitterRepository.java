package com.zerobase.hoops.alarm.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {

  public final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

  public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
    emitters.put(emitterId, sseEmitter);
    return sseEmitter;
  }

  public void saveEventCache(String emitterId, Object event) {
    eventCache.put(emitterId, event);
  }

  public Map<String, SseEmitter> findAllStartWithByUserId(String userId) {
    return emitters.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(userId))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Map<String, Object> findAllEventCacheStartWithUserId(
      String userId) {
    return eventCache.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(userId))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public void deleteAllStartWithUserId(String userId) {
    emitters.forEach(
        (key, value) -> {
          if(key.startsWith(userId)) {
            emitters.remove(key);
          }
        }
    );
  }

  public void deleteByEmitterId(String emitterId) {
    emitters.remove(emitterId);
  }

  public void deleteAllEventCacheStartWithUserId(String userId) {
    eventCache.forEach(
        (key, value) -> {
          if (key.startsWith(userId)) {
            eventCache.remove(key);
          }
        }
    );
  }
}
