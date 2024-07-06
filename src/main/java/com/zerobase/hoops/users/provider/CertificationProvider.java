package com.zerobase.hoops.users.provider;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CertificationProvider {

  public static String createCertificationNumber()
      throws NoSuchAlgorithmException {
    log.info("인증번호 생성 시작");
    String certificationNumber;

    int sum = SecureRandom.getInstanceStrong().nextInt(999999);
    certificationNumber = String.format("%06d", sum);

    log.info("인증번호 생성 완료");
    return certificationNumber;
  }
}
