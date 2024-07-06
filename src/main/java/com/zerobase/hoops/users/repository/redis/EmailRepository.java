package com.zerobase.hoops.users.repository.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailRepository {

  private final int EMAIL_LIMIT_TIME = 180;
  private final StringRedisTemplate redisTemplate;

  public void saveCertificationNumber(String email,
      String certificationNumber) {
    redisTemplate.opsForValue().set(email, certificationNumber,
        Duration.ofSeconds(EMAIL_LIMIT_TIME));
  }

  public String getCertificationNumber(String email) {
    return redisTemplate.opsForValue().get(email);
  }

  public void removeCertificationNumber(String email) {
    redisTemplate.delete(email);
  }

  public boolean hasKey(String email) {
    Boolean keyExists = redisTemplate.hasKey(email);
    return keyExists != null && keyExists;
  }

}
