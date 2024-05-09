package com.zerobase.hoops.users.oauth2.controller;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.users.dto.KakaoDto;
import com.zerobase.hoops.users.dto.KakaoDto.Response;
import com.zerobase.hoops.users.dto.TokenDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.oauth2.service.OAuth2Service;
import com.zerobase.hoops.users.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
      @RequestParam(name = "code") String code, HttpSession session)
      throws IOException {
    log.info("카카오 API 서버 code : " + code);
    UserDto userDto = oAuth2Service.kakaoLogin(code, session);
    TokenDto tokenDto = authService.getToken(userDto);

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Authorization", tokenDto.getAccessToken());

    return ResponseEntity.ok()
        .headers(responseHeaders)
        .body(KakaoDto.Response.fromDto(userDto, tokenDto.getRefreshToken()));
  }

  /**
   * 카카오 로그아웃
   */
  @Operation(summary = "카카오 로그아웃")
  @GetMapping("/logout/kakao")
  public ResponseEntity<HttpStatus> kakaoLogout(
      HttpServletRequest request,
      @AuthenticationPrincipal UserEntity userEntity
  ) {
    oAuth2Service.kakaoLogout(request, userEntity);
    authService.logOutUser(request, userEntity);

    log.info("kakao logout complete!");
    return ResponseEntity.ok(HttpStatus.OK);
  }
}
