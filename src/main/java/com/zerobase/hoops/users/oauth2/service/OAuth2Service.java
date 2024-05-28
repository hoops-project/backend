package com.zerobase.hoops.users.oauth2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.users.dto.KakaoDto;
import com.zerobase.hoops.users.dto.LogInDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.oauth2.dto.KakaoOAuthTokenDto;
import com.zerobase.hoops.users.oauth2.dto.KakaoUserInfoDto;
import com.zerobase.hoops.users.oauth2.dto.KakaoUserInfoDto.KakaoAccount;
import com.zerobase.hoops.users.oauth2.dto.KakaoUserInfoDto.Properties;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

  private final UserRepository userRepository;
  private final AuthService authService;

  @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
  String clientId;
  @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
  String clientSecret;
  @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
  String authorizationUri;
  @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
  String tokenRequestUri;
  @Value("${admin-key}")
  String adminId;

  public String responseUrl() {
    return authorizationUri + "?client_id=" + clientId +
        "&redirect_uri=https://hoops-frontend-jet.vercel.app/kakao/callback"
        + "&response_type=code";
  }

  public UserDto kakaoLogin(String code)
      throws IOException {
    KakaoUserInfoDto kakaoUser = getKakaoUserInfoDto(code);
    Properties properties = kakaoUser.getProperties();
    KakaoAccount kakaoAccount = kakaoUser.getKakao_account();
    String id = "kakao_" + kakaoUser.getId().toString();

    if (kakaoAccount.getEmail() == null && kakaoAccount.getGender() == null
        && !userRepository.existsByIdAndDeletedDateTimeNull("kakao_" + id)) {
      KakaoDto.Request user = kakaoUserDto(
          id,
          id + "@kakao.com",
          properties.getNickname()
          , "MALE");

      userRepository.save(KakaoDto.Request.toEntity(user));
    } else if (kakaoAccount.getEmail() == null && kakaoAccount.getGender() != null
        && !userRepository.existsByIdAndDeletedDateTimeNull("kakao_" + id)) {
      KakaoDto.Request user = kakaoUserDto(
          id,
          id + "@kakao.com",
          properties.getNickname()
          , kakaoAccount.getGender());

      userRepository.save(KakaoDto.Request.toEntity(user));
    } else if (kakaoAccount.getEmail() != null && kakaoAccount.getGender() == null
        && !userRepository.existsByIdAndDeletedDateTimeNull("kakao_" + id)) {
      KakaoDto.Request user = kakaoUserDto(
          id,
          kakaoAccount.getEmail(),
          properties.getNickname()
          , "MALE");

      userRepository.save(KakaoDto.Request.toEntity(user));
    } else if (!userRepository.existsByIdAndDeletedDateTimeNull("kakao_" + id)) {
      KakaoDto.Request user = kakaoUserDto(
          id,
          kakaoAccount.getEmail(),
          properties.getNickname()
          , kakaoAccount.getGender());

      userRepository.save(KakaoDto.Request.toEntity(user));
    }
    LogInDto.Request logInDto = kakaoUserLogin(id);
    return authService.logInUser(logInDto);
  }

  private KakaoUserInfoDto getKakaoUserInfoDto(String code)
      throws JsonProcessingException {
    ResponseEntity<String> accessTokenResponse = requestAccessToken(code);
    KakaoOAuthTokenDto kakaoOAuthTokenDto = getAccessToken(accessTokenResponse);
    ResponseEntity<String> userInfoResponse =
        requestUserInfo(kakaoOAuthTokenDto);

    return getUserInfo(userInfoResponse);
  }

  private KakaoDto.Request kakaoUserDto(String id, String email,
      String nickName, String gender) {
    return KakaoDto.Request.builder()
        .id(id)
        .email(email)
        .name(nickName)
        .nickName(nickName)
        .gender(gender.toUpperCase())
        .build();
  }

  public ResponseEntity<String> requestAccessToken(String code) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headersAccess = new HttpHeaders();
    headersAccess.add("Content-type",
        "application/x-www-form-urlencoded;charset=utf-8");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("redirect_uri", "https://hoops-frontend-jet.vercel.app/kakao/callback");
    params.add("code", code);

    HttpEntity<MultiValueMap<String, String>> kakaoRequest =
        new HttpEntity<>(params, headersAccess);

    return restTemplate.postForEntity(tokenRequestUri, kakaoRequest,
        String.class);
  }

  public KakaoOAuthTokenDto getAccessToken(ResponseEntity<String> response)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();

    return objectMapper.readValue(response.getBody(), KakaoOAuthTokenDto.class);
  }

  public ResponseEntity<String> requestUserInfo(
      KakaoOAuthTokenDto oAuthTokenDto) {
    HttpHeaders headers = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    headers.add("Authorization",
        "Bearer " + oAuthTokenDto.getAccess_token());

    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<>(headers);

    return restTemplate.exchange("https://kapi.kakao.com/v2/user/me",
        HttpMethod.GET, request, String.class);
  }

  public KakaoUserInfoDto getUserInfo(ResponseEntity<String> response)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(response.getBody(), KakaoUserInfoDto.class);
  }

  public LogInDto.Request kakaoUserLogin(String id) {
    return LogInDto.Request.builder()
        .id(id)
        .password("kakao")
        .build();
  }

  public void kakaoLogout(HttpServletRequest request,
      UserEntity userEntity) {
    String id = userEntity.getId();
    String kakaoId = id.substring(6);

    HttpHeaders headers = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    headers.add("Authorization", "KakaoAK " + adminId);

    request.getSession().removeAttribute("kakaoToken");
    request.getSession().removeAttribute("kakaoRefreshToken");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("target_id_type", "user_id");
    params.add("target_id", kakaoId);

    HttpEntity<MultiValueMap<String, String>> kakaoRequest =
        new HttpEntity<>(params, headers);

    restTemplate.exchange("https://kapi.kakao.com/v1/user/logout",
        HttpMethod.POST, kakaoRequest, String.class);
  }

  public void kakaoUnlink(HttpServletRequest request, UserEntity userEntity) {
    String id = userEntity.getId();
    String kakaoId = id.substring(6);

    HttpHeaders headers = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    headers.add("Authorization", "KakaoAK " + adminId);

    request.getSession().removeAttribute("kakaoToken");
    request.getSession().removeAttribute("kakaoRefreshToken");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("target_id_type", "user_id");
    params.add("target_id", kakaoId);

    HttpEntity<MultiValueMap<String, String>> kakaoRequest =
        new HttpEntity<>(params, headers);

    restTemplate.exchange("https://kapi.kakao.com/v1/user/unlink",
        HttpMethod.POST, kakaoRequest, String.class);
  }
}