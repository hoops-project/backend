package com.zerobase.hoops.users.controller;

import com.zerobase.hoops.users.dto.SignUpDto;
import com.zerobase.hoops.users.dto.SignUpDto.Response;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<Response> signUp(
      @RequestBody @Validated SignUpDto.Request request
  ) {
    UserDto signUpUser = userService.signUpUser(request);
    return ResponseEntity.ok(SignUpDto.Response.fromDto(signUpUser));
  }

  /**
   * ID 중복 검사
   */
  @Operation(summary = "ID 중복 검사")
  @GetMapping("/check/id")
  public ResponseEntity<Boolean> idCheck(
      @RequestParam(name = "id") String id
  ) {
    boolean idCheck = userService.idCheck(id);

    return ResponseEntity.ok(idCheck);
  }

  /**
   * EMAIL 중복 검사
   */
  @Operation(summary = "EMAIL 중복 검사")
  @GetMapping("/check/email")
  public ResponseEntity<Boolean> emailCheck(
      @RequestParam(name = "email") String email
  ) {
    boolean idCheck = userService.emailCheck(email);

    return ResponseEntity.ok(idCheck);
  }

  /**
   * 별명 중복 검사
   */
  @Operation(summary = "별명 중복 검사")
  @GetMapping("/check/nickname")
  public ResponseEntity<Boolean> nickNameCheck(
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
  public ResponseEntity<String> confirmCertificationNumber(
      @RequestParam(name = "loginId") String loginId,
      @RequestParam(name = "email") String email,
      @RequestParam(name = "certificationNumber") String certificationNumber
  ) {
    userService.confirmEmail(loginId, email, certificationNumber);

    return ResponseEntity.ok("인증이 성공적으로 완료되었습니다.");
  }

  /**
   * 아이디 찾기
   */
  @Operation(summary = "아이디 찾기")
  @GetMapping("/find/id")
  public ResponseEntity<String> findLoginId(
      @RequestParam(name = "email") String email
  ) {
    String loginId = userService.findLoginId(email);

    return ResponseEntity.ok(loginId);
  }

  /**
   * 비밀번호 찾기
   */
  @Operation(summary = "비밀번호 찾기")
  @GetMapping("/find/password")
  public ResponseEntity<Boolean> findPassword(
      @RequestParam(name = "loginId") String loginId
  ) throws NoSuchAlgorithmException {
    boolean success = userService.findPassword(loginId);

    return ResponseEntity.ok(success);
  }
}
