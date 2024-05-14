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

  public Map<String, SseEmitter> findAllStartWithByEmitterId(String emitterId) {
    return emitters.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(emitterId))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Map<String, Object> findAllEventCacheStartWithEmitterId(
      String emitterId) {
    return eventCache.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(emitterId))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public void deleteAllStartWithEmitterId(String emitterId) {
    emitters.forEach(
        (key, value) -> {
          if(key.startsWith(emitterId)) {
            emitters.remove(key);
          }
        }
    );
  }

  public void deleteByEmitterId(String emitterId) {
    emitters.remove(emitterId);
  }

  public void deleteAllEventCacheStartWithEmitterId(String emitterId) {
    eventCache.forEach(
        (key, value) -> {
          if (key.startsWith(emitterId)) {
            eventCache.remove(key);
          }
        }
    );
  }
}
