package com.zerobase.hoops.users.provider;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class CertificationProvider {

  public static String createCertificationNumber()
      throws NoSuchAlgorithmException {
    String certificationNumber;

    int sum = SecureRandom.getInstanceStrong().nextInt(999999);
    certificationNumber = String.format("%06d", sum);

    return certificationNumber;
  }
}
