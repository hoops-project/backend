package com.zerobase.hoops.users.controller;

import com.zerobase.hoops.users.dto.LogInDto;
import com.zerobase.hoops.users.dto.TokenDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

  /**
   * 로그인
   */
  @Operation(summary = "로그인")
  @PostMapping("/login")
  public ResponseEntity<?> logIn(
      @RequestBody @Validated LogInDto.Request request
  ) {
    UserDto userDto = authService.logInUser(request);

    TokenDto tokenDto = authService.getToken(userDto);

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Authorization", tokenDto.getAccessToken());

    return ResponseEntity.ok()
        .headers(responseHeaders)
        .body(LogInDto.Response.fromDto(userDto, tokenDto.getRefreshToken()));
  }
}
