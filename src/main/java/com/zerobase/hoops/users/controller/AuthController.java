package com.zerobase.hoops.users.controller;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.manager.service.ManagerService;
import com.zerobase.hoops.users.dto.EditDto;
import com.zerobase.hoops.users.dto.LogInDto;
import com.zerobase.hoops.users.dto.LogInDto.Response;
import com.zerobase.hoops.users.dto.TokenDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.oauth2.service.OAuth2Service;
import com.zerobase.hoops.users.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "2. AUTH")
public class AuthController {

  private final AuthService authService;
  private final OAuth2Service oAuth2Service;
  private final ManagerService managerService;

  /**
   * 로그인
   */
  @Operation(summary = "로그인")
  @PostMapping("/login")
  public ResponseEntity<Response> logIn(
      @RequestBody @Validated LogInDto.Request request
  ) {
    log.info("로그인 요청");
    log.info("블랙리스트 여부 확인");
    managerService.checkBlackList(request.getLoginId());
    UserDto userDto = authService.logInUser(request);
    TokenDto tokenDto = authService.getToken(userDto);

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Authorization", tokenDto.getAccessToken());

    log.info("로그인 성공 : {}", userDto.getLoginId());
    return ResponseEntity.ok()
        .headers(responseHeaders)
        .body(LogInDto.Response.fromDto(userDto, tokenDto.getRefreshToken()));
  }

  /**
   * refresh
   */
  @Operation(summary = "refresh")
  @PostMapping("/refresh-token")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<Response> refreshToken(
      HttpServletRequest request,
      @AuthenticationPrincipal UserEntity userEntity
  ) {
    log.info("토큰 갱신 요청");
    TokenDto tokenDto = authService.refreshToken(request, userEntity);
    UserDto userDto = authService.getUserInfo(request, userEntity);

    HttpHeaders responseAccessToken = new HttpHeaders();
    responseAccessToken.set("Authorization", tokenDto.getAccessToken());

    log.info("토큰 갱신 성공 : {}", userDto.getLoginId());
    return ResponseEntity.ok()
        .headers(responseAccessToken)
        .body(LogInDto.Response.fromDto(userDto, tokenDto.getRefreshToken()));
  }

  /**
   * 로그아웃
   */
  @Operation(summary = "로그아웃")
  @PostMapping("/logout")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<HttpStatus> logOut(
      HttpServletRequest request,
      @AuthenticationPrincipal UserEntity userEntity
  ) {
    log.info("로그아웃 요청");
    if (userEntity.getLoginId().startsWith("kakao_")) {
      log.info("카카오 로그아웃");
      oAuth2Service.kakaoLogout(request, userEntity);
    }
    authService.logOutUser(request, userEntity);
    log.info("로그아웃 성공 : {}", userEntity.getLoginId());

    return ResponseEntity.ok(HttpStatus.OK);
  }

  /**
   * 회원 정보 조회
   */
  @Operation(summary = "회원 정보 조회")
  @GetMapping("/user/info")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<UserDto> getUserInfo(
      HttpServletRequest request,
      @AuthenticationPrincipal UserEntity user
  ) {
    log.info("회원 정보 조회 요청");
    UserDto userDto = authService.getUserInfo(request, user);

    log.info("회원 정보 조회 성공 : {}", user.getLoginId());
    return ResponseEntity.ok(userDto);
  }

  /**
   * 회원 정보 수정
   */
  @Operation(summary = "회원 정보 수정")
  @PatchMapping("/user/edit")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<EditDto.Response> editUserInfo(
      HttpServletRequest request,
      @RequestBody @Validated EditDto.Request editDto,
      @AuthenticationPrincipal UserEntity user
  ) {
    log.info("회원 정보 수정 요청");
    UserDto userDto = authService.editUserInfo(request, editDto, user);
    TokenDto tokenDto = authService.getToken(userDto);

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Authorization", tokenDto.getAccessToken());

    log.info("회원 정보 수정 성공 : {}", user.getLoginId());
    return ResponseEntity.ok()
        .headers(responseHeaders)
        .body(EditDto.Response.fromDto(userDto, tokenDto.getRefreshToken()));
  }

  /**
   * 회원 탈퇴
   */
  @Operation(summary = "회원 탈퇴")
  @PostMapping("/deactivate")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<HttpStatus> deactivateUser(
      HttpServletRequest request,
      @AuthenticationPrincipal UserEntity user
  ) {
    log.info("회원 탈퇴 요청");
    if (user != null && user.getLoginId().startsWith("kakao")) {
      log.info("카카오 회원 탈퇴");
      oAuth2Service.kakaoLogout(request, user);
      oAuth2Service.kakaoUnlink(request, user);
    }

    authService.deactivateUser(request, user);

    log.info("회원 탈퇴 성공 : {}", user.getLoginId());
    return ResponseEntity.ok(HttpStatus.OK);
  }
}
