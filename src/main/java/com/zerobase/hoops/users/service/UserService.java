package com.zerobase.hoops.users.service;

import com.zerobase.hoops.document.UserDocument;
import com.zerobase.hoops.users.dto.SignUpDto.Request;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.users.provider.CertificationProvider;
import com.zerobase.hoops.users.provider.EmailProvider;
import com.zerobase.hoops.users.repository.redis.EmailRepository;
import com.zerobase.hoops.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final EmailRepository emailRepository;
  private final EmailProvider emailProvider;
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public UserDto signUpUser(Request request) {
    log.info("회원 가입 시작: {}", request.getLoginId());
    if (!request.getPassword().equals(request.getPasswordCheck())) {
      log.error("회원가입 에러 : {}", ErrorCode.NOT_MATCHED_PASSWORD.getDescription());
      throw new CustomException(ErrorCode.NOT_MATCHED_PASSWORD);
    }

    String password = request.getPassword();
    String encodedPassword = passwordEncoder.encode(password);
    request.setPassword(encodedPassword);

    try {
      String loginId = request.getLoginId();
      String email = request.getEmail();
      String nickName = request.getNickName();

      idCheck(loginId);
      emailCheck(email);
      nickNameCheck(nickName);

      String certificationNumber =
          CertificationProvider.createCertificationNumber();
      log.info("인증 번호 생성: {}", certificationNumber);
      try {
        emailProvider.sendCertificationMail(loginId, email,
            certificationNumber);
      } catch (Exception e) {
        e.printStackTrace();
      }
      emailRepository.saveCertificationNumber(email, certificationNumber);

      long userId = userRepository.count() + 1;

      UserDocument signUpUser =
          userRepository.save(Request.toDocument(request, userId));

      log.info("회원 가입 완료: {}", signUpUser.getLoginId());

      return UserDto.fromDocument(signUpUser);
    } catch (NoSuchAlgorithmException e) {
      log.error("암호화 에러 : {}", e);
      throw new RuntimeException(e);
    }
  }

  public boolean idCheck(String loginId) {
    log.info("ID 중복 검사 시작: {}", loginId);
    boolean isExistId = userRepository.existsByLoginIdAndDeletedDateTimeNull(loginId);
    if (isExistId) {
      log.error("ID 중복");
      throw new CustomException(ErrorCode.DUPLICATED_ID);
    }

    log.info("ID 중복 검사 완료: {}", loginId);
    return true;
  }

  public boolean emailCheck(String email) {
    log.info("EMAIL 중복 검사 시작: {}", email);
    boolean isExistEmail =
        userRepository.existsByEmailAndDeletedDateTimeNull(email);
    if (isExistEmail) {
      log.error("EMAIL 중복");
      throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
    }

    log.info("EMAIL 중복 검사 완료: {}", email);
    return true;
  }

  public boolean nickNameCheck(String nickName) {
    log.info("별명 중복 검사 시작: {}", nickName);
    boolean isExistNickname =
        userRepository.existsByNickNameAndDeletedDateTimeNull(nickName);
    if (isExistNickname) {
      log.error("별명 중복");
      throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
    }

    log.info("별명 중복 검사 완료: {}", nickName);
    return true;
  }

  public void confirmEmail(
      String loginId, String email, String certificationNumber) {
    log.info("이메일 인증 시작: {}, {}", loginId, email);
    if (!checkCertificationNumber(email, certificationNumber)) {
      log.error("인증 번호 불일치");
      throw new CustomException(ErrorCode.NOT_MATCHED_NUMBER);
    }

    UserDocument user = userRepository.findByLoginIdAndDeletedDateTimeNull(loginId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    user.confirm();

    emailRepository.removeCertificationNumber(email);
    userRepository.save(user);
    log.info("이메일 인증 완료: {}, {}", loginId, email);
  }

  private boolean checkCertificationNumber(
      String email, String certificationNumber) {
    boolean validatedEmail = emailRepository.hasKey(email);
    if (!validatedEmail) {
      log.error("인증 번호 만료");
      throw new CustomException(ErrorCode.WRONG_EMAIL);
    }

    return emailRepository
        .getCertificationNumber(email)
        .equals(certificationNumber);
  }

  @Override
  public UserDetails loadUserByUsername(String loginId)
      throws UsernameNotFoundException {
    return userRepository.findByLoginIdAndDeletedDateTimeNull(loginId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }


  public String findLoginId(String email) {
    log.info("ID 찾기 시작: {}", email);
    UserDocument user =
        userRepository.findByEmailAndDeletedDateTimeNull(email)
            .orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));

    log.info("ID 찾기 완료: {}", email);
    return user.getLoginId();
  }

  public boolean findPassword(String loginId) throws NoSuchAlgorithmException {
    log.info("비밀번호 찾기 시작: {}", loginId);
    UserDocument user =
        userRepository.findByLoginIdAndDeletedDateTimeNull(loginId)
            .orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));

    String newPassword =
        "Gnqtm!" + CertificationProvider.createCertificationNumber();
    String encodedNewPassword = passwordEncoder.encode(newPassword);
    user.passwordEdit(encodedNewPassword);
    userRepository.save(user);

    String email = user.getEmail();

    try {
      emailProvider.sendTemporaryPasswordMail(email, newPassword);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    log.info("비밀번호 찾기 완료: {}", loginId);
    return true;
  }
}
