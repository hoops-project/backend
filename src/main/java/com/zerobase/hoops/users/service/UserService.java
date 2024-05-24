package com.zerobase.hoops.users.service;

import com.zerobase.hoops.entity.UserEntity;
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
    if (!request.getPassword().equals(request.getPasswordCheck())) {
      throw new CustomException(ErrorCode.NOT_MATCHED_PASSWORD);
    }

    String password = request.getPassword();
    String encodedPassword = passwordEncoder.encode(password);
    request.setPassword(encodedPassword);

    try {
      String id = request.getId();
      String email = request.getEmail();
      String nickName = request.getNickName();

      boolean isExistId = idCheck(id);
      boolean isExistEmail = emailCheck(email);
      boolean isExistNickName = nickNameCheck(nickName);

      if (!isExistId) {
        throw new CustomException(ErrorCode.DUPLICATED_ID);
      } else if (!isExistEmail) {
        throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
      } else if (!isExistNickName) {
        throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
      }

      String certificationNumber =
          CertificationProvider.createCertificationNumber();
      boolean isSuccess =
          emailProvider.sendCertificationMail(id, email,
              certificationNumber);
      if (!isSuccess) {
        throw new CustomException(ErrorCode.MAIL_SEND_FAIL);
      }
      emailRepository.saveCertificationNumber(email, certificationNumber);

      UserEntity signUpUser =
          userRepository.save(Request.toEntity(request));

      return UserDto.fromEntity(signUpUser);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean idCheck(String id) {
    boolean isExistId = userRepository.existsByIdAndDeletedDateTimeNull(id);
    if (isExistId) {
      throw new CustomException(ErrorCode.DUPLICATED_ID);
    }

    return true;
  }

  public boolean emailCheck(String email) {
    boolean isExistEmail =
        userRepository.existsByEmailAndDeletedDateTimeNull(email);
    if (isExistEmail) {
      throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
    }

    return true;
  }

  public boolean nickNameCheck(String nickName) {
    boolean isExistNickname =
        userRepository.existsByNickNameAndDeletedDateTimeNull(nickName);
    if (isExistNickname) {
      throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
    }

    return true;
  }

  public void confirmEmail(
      String id, String email, String certificationNumber) {
    if (!checkCertificationNumber(email, certificationNumber)) {
      throw new CustomException(ErrorCode.NOT_MATCHED_NUMBER);
    }

    UserEntity user = userRepository.findByIdAndDeletedDateTimeNull(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    user.confirm();

    emailRepository.removeCertificationNumber(email);
    userRepository.save(user);
  }

  private boolean checkCertificationNumber(
      String email, String certificationNumber) {
    boolean validatedEmail = emailRepository.hasKey(email);
    if (!validatedEmail) {
      throw new CustomException(ErrorCode.WRONG_EMAIL);
    }

    return emailRepository
        .getCertificationNumber(email)
        .equals(certificationNumber);
  }

  public UserDto getUserInfo(String id) {
    UserEntity userEntity = userRepository.findByIdAndDeletedDateTimeNull(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    return UserDto.fromEntity(userEntity);
  }

  @Override
  public UserDetails loadUserByUsername(String id)
      throws UsernameNotFoundException {
    return userRepository.findByIdAndDeletedDateTimeNull(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }


  public String findId(String email) {
    UserEntity user =
        userRepository.findByEmailAndDeletedDateTimeNull(email)
            .orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));

    return user.getId();
  }

  public boolean findPassword(String id) throws NoSuchAlgorithmException {
    UserEntity user =
        userRepository.findByIdAndDeletedDateTimeNull(id)
            .orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));

    String newPassword =
        "Gnqtm!" + CertificationProvider.createCertificationNumber();
    String encodedNewPassword = passwordEncoder.encode(newPassword);
    user.passwordEdit(encodedNewPassword);
    userRepository.save(user);

    String email = user.getEmail();

    boolean isSuccess =
        emailProvider.sendTemporaryPasswordMail(email, newPassword);
    if (!isSuccess) {
      throw new CustomException(ErrorCode.MAIL_SEND_FAIL);
    }

    return isSuccess;
  }
}
