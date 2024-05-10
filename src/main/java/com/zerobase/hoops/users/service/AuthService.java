package com.zerobase.hoops.users.service;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.dto.EditDto;
import com.zerobase.hoops.users.dto.LogInDto;
import com.zerobase.hoops.users.dto.TokenDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.repository.AuthRepository;
import com.zerobase.hoops.users.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {

  private final AuthRepository authRepository;
  private final UserRepository userRepository;

  private final TokenProvider tokenProvider;

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public UserDto logInUser(LogInDto.Request request) {

    UserEntity user =
        userRepository.findByIdAndDeletedDateTimeNull(request.getId())
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

  public TokenDto refreshToken(
      HttpServletRequest request, UserEntity userEntity
  ) {

    String expiredAccessToken = validateAccessTokenExistHeader(request);
    String refreshToken = validateRefreshTokenExistHeader(request);

    Claims claims = tokenProvider.parseClaims(refreshToken);
    String id = claims.get("id", String.class);
    String email = claims.get("sub", String.class);
    List<String> roles = (List<String>) claims.get("roles");

    if (!tokenUserMatch(expiredAccessToken, refreshToken)) {
      throw new CustomException(ErrorCode.NOT_MATCHED_TOKEN);
    }

    if (!userEntity.getId().equals(id)) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    try {
      authRepository.findById(id);
    } catch (Exception e) {
      throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
    }

    userRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    String responseAccessToken =
        tokenProvider.createAccessToken(id, email, roles);

    return new TokenDto(id, responseAccessToken, refreshToken);
  }

  private String validateAccessTokenExistHeader(HttpServletRequest request) {
    String token = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (!ObjectUtils.isEmpty(token) && token.startsWith("Bearer ")) {
      return token.substring("Bearer ".length());
    } else {
      throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
    }
  }

  private String validateRefreshTokenExistHeader(HttpServletRequest request) {
    String token = request.getHeader("refreshToken");
    if (!ObjectUtils.isEmpty(token)) {
      return token;
    } else {
      throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
    }
  }

  public void logOutUser(
      HttpServletRequest request, UserEntity userEntity) {
    String accessToken = validateAccessTokenExistHeader(request);
    String refreshToken = validateRefreshTokenExistHeader(request);

    Claims claims = tokenProvider.parseClaims(accessToken);
    String id = claims.get("id", String.class);

    if (tokenUserMatch(accessToken, refreshToken) &&
        id.equals(userEntity.getId())) {
      authRepository.deleteById(id);
    } else {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    tokenProvider.addToLogOutList(accessToken);
  }

  private boolean tokenUserMatch(String accessToken, String refreshToken) {
    Claims accessClaims = tokenProvider.parseClaims(accessToken);
    Claims refreshClaims = tokenProvider.parseClaims(refreshToken);
    String accessId = accessClaims.get("id", String.class);
    String refreshId = refreshClaims.get("id", String.class);

    return accessId.equals(refreshId);
  }

  public UserDto getUserInfo(HttpServletRequest request, UserEntity user) {
    isSameId(request, user);
    return UserDto.fromEntity(user);
  }

  public UserDto editUserInfo(HttpServletRequest request,
      EditDto.Request editDto, UserEntity user) {
    isSameId(request, user);

    if(editDto.getPassword() != null) {
      String encodedNewPassword = passwordEncoder.encode(editDto.getPassword());
      user.passwordEdit(encodedNewPassword);
    }

    user.edit(editDto);
    userRepository.save(user);

    return UserDto.fromEntity(user);
  }

  private void isSameId(HttpServletRequest request, UserEntity user) {
    String accessToken = validateAccessTokenExistHeader(request);

    Claims claims = tokenProvider.parseClaims(accessToken);
    String id = claims.get("id", String.class);

    if (!user.getId().equals(id)) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }
}
