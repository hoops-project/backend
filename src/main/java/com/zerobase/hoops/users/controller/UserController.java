package com.zerobase.hoops.users.controller;

import com.zerobase.hoops.users.dto.SignUpDto;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "1. USER")
public class UserController {

  private final UserService userService;

  /**
   * 회원 가입
   */
  @Operation(summary = "회원 가입")
  @PostMapping("/signup")
  public ResponseEntity<?> signUp(
      @RequestBody @Validated SignUpDto.Request request
  ) {
    UserDto signUpUser = userService.signUpUser(request);
    return ResponseEntity.ok(SignUpDto.Response.fromDto(signUpUser));
  }

  /**
   * ID 중복 검사
   */
  @Operation(summary = "ID 중복 검사")
  @PostMapping("/check/id")
  public ResponseEntity<?> idCheck(
      @RequestParam(name = "id") String id
  ) {
    boolean idCheck = userService.idCheck(id);

    return ResponseEntity.ok(idCheck);
  }

  /**
   * EMAIL 중복 검사
   */
  @Operation(summary = "EMAIL 중복 검사")
  @PostMapping("/check/email")
  public ResponseEntity<?> emailCheck(
      @RequestParam(name = "email") String email
  ) {
    boolean idCheck = userService.emailCheck(email);

    return ResponseEntity.ok(idCheck);
  }

  /**
   * 별명 중복 검사
   */
  @Operation(summary = "별명 중복 검사")
  @PostMapping("/check/nickname")
  public ResponseEntity<?> nickNameCheck(
      @RequestParam(name = "nickName") String nickName
  ) {
    boolean nickNameCheck = userService.nickNameCheck(nickName);

    return ResponseEntity.ok(nickNameCheck);
  }

  /**
   * 이메일 인증
   */
  @Operation(summary = "이메일 인증")
  @GetMapping("/signup/confirm")
  public ResponseEntity<?> confirmCertificationNumber(
      @RequestParam(name = "id") String id,
      @RequestParam(name = "email") String email,
      @RequestParam(name = "certificationNumber") String certificationNumber
  ) throws Exception {
    userService.confirmEmail(id, email, certificationNumber);

    URI loginPage = new URI("/api/user/login");

    return ResponseEntity.status(HttpStatus.FOUND).location(loginPage).build();
  }

}
