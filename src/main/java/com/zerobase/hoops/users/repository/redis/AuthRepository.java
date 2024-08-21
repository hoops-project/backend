package com.zerobase.hoops.users.repository.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthRepository {

  private final StringRedisTemplate redisTemplate;

  public void saveRefreshToken(
      String loginId, String refreshToken, Duration duration
  ) {
    redisTemplate.opsForValue().set(loginId, refreshToken, duration);
  }

  public void findByLoginId(String loginId) {
    redisTemplate.opsForValue().get(loginId);
  }

  public void deleteByLoginId(String loginId) {
    redisTemplate.delete(loginId);
  }
}
