package com.zerobase.hoops.users.service;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.dto.LogInDto;
import com.zerobase.hoops.users.dto.TokenDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {

  private final UserRepository userRepository;

  private final TokenProvider tokenProvider;

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public UserDto logInUser(LogInDto.Request request) {

    UserEntity user =
        userRepository.findByIdAndDeleteDateTimeNull(request.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    String password = request.getPassword();
    String encodedPassword = user.getPassword();
    boolean isMatched = passwordEncoder.matches(password, encodedPassword);
    if (!isMatched) {
      throw new CustomException(ErrorCode.NOT_MATCHED_PASSWORD);
    }

    if (!user.isEmailAuth()) {
      throw new CustomException(ErrorCode.USER_NOT_CONFIRM);
    }

    return UserDto.fromEntity(user);
  }

  public TokenDto getToken(UserDto userDto) {
    String accessToken =
        tokenProvider.createAccessToken(userDto.getId(),
            userDto.getEmail(), userDto.getRoles());
    String refreshToken =
        tokenProvider.createRefreshToken(userDto.getId(),
            userDto.getEmail(), userDto.getRoles());

    return new TokenDto(userDto.getId(), accessToken, refreshToken);
  }
}
