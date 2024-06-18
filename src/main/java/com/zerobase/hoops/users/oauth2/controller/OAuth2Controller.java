package com.zerobase.hoops.users.oauth2.controller;

import com.zerobase.hoops.users.dto.KakaoDto;
import com.zerobase.hoops.users.dto.KakaoDto.Response;
import com.zerobase.hoops.users.dto.TokenDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.oauth2.service.OAuth2Service;
import com.zerobase.hoops.users.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "5. OAUTH2")
public class OAuth2Controller {

  private final OAuth2Service oAuth2Service;
  private final AuthService authService;

  /**
   * 카카오 로그인
   */
  @Operation(summary = "카카오 로그인")
  @GetMapping("/login/kakao")
  public void getKakaoAuthUrl(HttpServletResponse response) throws IOException {
    response.sendRedirect(oAuth2Service.responseUrl());
  }

  @GetMapping("/kakao")
  public ResponseEntity<Response> kakaoLogin(
      @RequestParam(name = "code") String code)
      throws IOException {
    log.info("카카오 로그인 시작");
    UserDto userDto = oAuth2Service.kakaoLogin(code);
    log.info("토큰 요청");
    TokenDto tokenDto = authService.getToken(userDto);

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Authorization", tokenDto.getAccessToken());

    log.info("카카오 로그인 완료");
    return ResponseEntity.ok()
        .headers(responseHeaders)
        .body(KakaoDto.Response.fromDto(userDto, tokenDto.getRefreshToken()));
  }

}
