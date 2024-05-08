package com.zerobase.hoops.users.oauth2.controller;

import com.zerobase.hoops.users.dto.KakaoDto;
import com.zerobase.hoops.users.dto.KakaoDto.Response;
import com.zerobase.hoops.users.dto.TokenDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.oauth2.service.OAuthService;
import com.zerobase.hoops.users.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

  private final OAuthService oAuth2Service;
  private final AuthService authService;

  @GetMapping("/login/kakao")
  public void getKakaoAuthUrl(HttpServletResponse response) throws IOException {
    response.sendRedirect(oAuth2Service.responseUrl());
  }

  @GetMapping("/kakao")
  public ResponseEntity<Response> kakaoLogin(
      @RequestParam(name = "code") String code) throws IOException {
    log.info("카카오 API 서버 code : " + code);
    UserDto userDto = oAuth2Service.kakaoLogin(code);
    TokenDto tokenDto = authService.getToken(userDto);

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Authorization", tokenDto.getAccessToken());

    return ResponseEntity.ok()
        .headers(responseHeaders)
        .body(KakaoDto.Response.fromDto(userDto, tokenDto.getRefreshToken()));
  }
}
