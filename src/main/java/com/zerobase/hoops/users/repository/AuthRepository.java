package com.zerobase.hoops.users.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthRepository {

  private final StringRedisTemplate redisTemplate;

  public void saveRefreshToken(
      String id, String refreshToken, Duration duration
  ) {
    redisTemplate.opsForValue().set(id, refreshToken, duration);
  }
}
