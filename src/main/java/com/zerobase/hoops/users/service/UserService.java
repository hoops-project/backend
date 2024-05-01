package com.zerobase.hoops.users.service;

import com.zerobase.hoops.entity.BlackListUserEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.users.dto.SignUpDto.Request;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.users.provider.CertificationProvider;
import com.zerobase.hoops.users.provider.EmailProvider;
import com.zerobase.hoops.users.repository.BlackListUserRepository;
import com.zerobase.hoops.users.repository.EmailRepository;
import com.zerobase.hoops.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
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
  private final BlackListUserRepository blackListUserRepository;
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
    } catch (CustomException e) {
      throw new CustomException(e.getErrorCode(), e.getErrorMessage());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean idCheck(String id) {
    try {
      boolean isExistId = userRepository.existsById(id);
      if (isExistId) {
        throw new CustomException(ErrorCode.DUPLICATED_ID);
      }
    } catch (CustomException e) {
      throw new CustomException(e.getErrorCode(), e.getErrorMessage());
    }

    return true;
  }

  public boolean emailCheck(String email) {
    try {
      boolean isExistEmail =
          userRepository.existsByEmail(email);
      if (isExistEmail) {
        throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
      }
    } catch (CustomException e) {
      throw new CustomException(e.getErrorCode(), e.getErrorMessage());
    }

    return true;
  }

  public boolean nickNameCheck(String nickName) {
    try {
      boolean isExistNickname =
          userRepository.existsByNickName(nickName);
      if (isExistNickname) {
        throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
      }
    } catch (CustomException e) {
      throw new CustomException(e.getErrorCode(), e.getErrorMessage());
    }

    return true;
  }

  public void confirmEmail(
      String id, String email, String certificationNumber) {
    if (!isConfirm(email, certificationNumber)) {
      throw new CustomException(ErrorCode.INVALID_NUMBER);
    }

    UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    user.confirm();

    emailRepository.removeCertificationNumber(email);
    userRepository.save(user);
  }

  private boolean isConfirm(String email, String certificationNumber) {
    boolean validatedEmail = emailRepository.hasKey(email);
    if (!validatedEmail) {
      throw new CustomException(ErrorCode.WRONG_EMAIL);
    }

    return (validatedEmail && emailRepository
        .getCertificationNumber(email)
        .equals(certificationNumber)
    );
  }

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {
    return this.userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }

  public void checkBlackList(String email) {
    Optional<BlackListUserEntity> blackUser = this.blackListUserRepository.findByEmail(
        email);
    if (blackUser.isPresent()) {
      int comparison = blackUser.get().getEndedAt()
          .compareTo(LocalDate.now());
      if (comparison > 0) {
        // 아직 시간 안지남
        throw new CustomException(ErrorCode.BAN_FOR_10DAYS);
      } else {
        // 시간 지남
        this.blackListUserRepository.deleteAllById(
            Collections.singleton(blackUser.get().getId()));
      }
    }
  }

}
