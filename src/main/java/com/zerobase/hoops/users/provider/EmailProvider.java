package com.zerobase.hoops.users.provider;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailProvider {

  private final JavaMailSender javaMailSender;
  private final String SUBJECT = "[HOOPS] 인증 메일입니다.";
  private final String PASSWORD_SUBJECT = "[HOOPS] 임시 비밀번호입니다.";

  public boolean sendCertificationMail(String loginId, String email,
      String certificationNumber) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper messageHelper =
          new MimeMessageHelper(message, true);

      String htmlContent = getCertificationMessage(
          loginId, email, certificationNumber);

      messageHelper.setTo(email);
      messageHelper.setSubject(SUBJECT);
      messageHelper.setText(htmlContent, true);

      javaMailSender.send(message);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private String getCertificationMessage(String loginId, String email,
      String certificationNumber) {
    String certificationMessage = "";
    certificationMessage +=
        "<h1 style='text-align: center;'>"
            + "[HOOPS] 인증 메일"
            + "</h1>";
    certificationMessage +=
        "<h3 style='text-align: center;'>"
            + "인증 링크 : "
            + "<a href=\"https://hoops.services/api/user/signup/confirm?loginId=" + loginId
            + "&email=" + email + "&certificationNumber=" + certificationNumber
            + "\">"
            + "이곳을 눌러 인증을 완료해주세요. 링크는 3분 동안 유효합니다."
            + "</a>"
            + "</h3>";

    return certificationMessage;
  }

  public boolean sendTemporaryPasswordMail(String email, String newPassword) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper messageHelper =
          new MimeMessageHelper(message, true);

      String htmlContent = getTemporaryPasswordMessage(newPassword);

      messageHelper.setTo(email);
      messageHelper.setSubject(PASSWORD_SUBJECT);
      messageHelper.setText(htmlContent, true);

      javaMailSender.send(message);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private String getTemporaryPasswordMessage(String newPassword) {
    String certificationMessage = "";
    certificationMessage +=
        "<h1 style='text-align: center;'>"
            + "[HOOPS] 임시 비밀번호 발송"
            + "</h1>";
    certificationMessage +=
        "<h3 style='text-align: center;'>"
            + "임시 비밀번호 : " + newPassword
            + "</h3>"
            + "<h4 style='text-align: center;'>"
            + "임시 비밀번호로 로그인 후 비밀번호를 꼭 변경해주세요."
            + "</h4>";

    return certificationMessage;
  }

}
