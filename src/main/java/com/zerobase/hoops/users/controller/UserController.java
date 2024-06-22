package com.zerobase.hoops.users.controller;

import com.zerobase.hoops.users.dto.CheckDto;
import com.zerobase.hoops.users.dto.FindDto.findIdResponse;
import com.zerobase.hoops.users.dto.FindDto.findPasswordResponse;
import com.zerobase.hoops.users.dto.SignUpDto;
import com.zerobase.hoops.users.dto.SignUpDto.Response;
import com.zerobase.hoops.users.dto.UserDto;
import com.zerobase.hoops.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "USER")
public class UserController {

  private final UserService userService;

  /**
   * 회원 가입
   */
  @Operation(summary = "회원 가입")
  @ApiResponse(responseCode = "200", description = "회원 가입 성공",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = Response.class))})
  @PostMapping("/signup")
  public ResponseEntity<Response> signUp(
      @RequestBody @Validated SignUpDto.Request request
  ) {
    log.info("회원 가입 요청");
    UserDto signUpUser = userService.signUpUser(request);
    log.info("회원 가입 성공 : {}", signUpUser.getLoginId());
    return ResponseEntity.ok(SignUpDto.Response.fromDto(signUpUser));
  }

  /**
   * ID 중복 검사
   */
  @Operation(summary = "ID 중복 검사")
  @ApiResponse(responseCode = "200", description = "ID 중복 검사 성공",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = CheckDto.class))})
  @GetMapping("/check/id")
  public ResponseEntity<CheckDto> idCheck(
      @RequestParam(name = "id") String id
  ) {
    log.info("ID 중복 검사");
    boolean idCheck = userService.idCheck(id);
    log.info("사용 ID : {}", id);

    return ResponseEntity.ok(CheckDto.fromBoolean(idCheck));
  }

  /**
   * EMAIL 중복 검사
   */
  @Operation(summary = "EMAIL 중복 검사")
  @ApiResponse(responseCode = "200", description = "EMAIL 중복 검사 성공",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = CheckDto.class))})
  @GetMapping("/check/email")
  public ResponseEntity<CheckDto> emailCheck(
      @RequestParam(name = "email") String email
  ) {
    log.info("EMAIL 중복 검사");
    boolean idCheck = userService.emailCheck(email);
    log.info("사용 EMAIL : {}", email);

    return ResponseEntity.ok(CheckDto.fromBoolean(idCheck));
  }

  /**
   * 별명 중복 검사
   */
  @Operation(summary = "별명 중복 검사")
  @ApiResponse(responseCode = "200", description = "별명 중복 검사 성공",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = CheckDto.class))})
  @GetMapping("/check/nickname")
  public ResponseEntity<CheckDto> nickNameCheck(
      @RequestParam(name = "nickName") String nickName
  ) {
    log.info("별명 중복 검사");
    boolean nickNameCheck = userService.nickNameCheck(nickName);
    log.info("사용 별명 : {}", nickName);

    return ResponseEntity.ok(CheckDto.fromBoolean(nickNameCheck));
  }

  /**
   * 이메일 인증
   */
  @Operation(summary = "이메일 인증")
  @ApiResponse(responseCode = "200", description = "이메일 인증 성공")
  @GetMapping("/signup/confirm")
  public ResponseEntity<String> confirmCertificationNumber(
      @RequestParam(name = "loginId") String loginId,
      @RequestParam(name = "email") String email,
      @RequestParam(name = "certificationNumber") String certificationNumber
  ) {
    log.info("이메일 인증 요청");
    userService.confirmEmail(loginId, email, certificationNumber);
    log.info("이메일 인증 성공");

    return ResponseEntity.ok("인증이 성공적으로 완료되었습니다.");
  }

  /**
   * 아이디 찾기
   */
  @Operation(summary = "아이디 찾기")
  @ApiResponse(responseCode = "200", description = "아이디 찾기 성공",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = findIdResponse.class))})
  @GetMapping("/find/id")
  public ResponseEntity<findIdResponse> findLoginId(
      @RequestParam(name = "email") String email
  ) {
    log.info("아이디 찾기 요청");
    String loginId = userService.findLoginId(email);
    log.info("찾은 아이디 : {}", loginId);

    return ResponseEntity.ok(findIdResponse.builder().loginId(loginId).build());
  }

  /**
   * 비밀번호 찾기
   */
  @Operation(summary = "비밀번호 찾기")
  @ApiResponse(responseCode = "200", description = "비밀번호 찾기 성공",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = findPasswordResponse.class))})
  @GetMapping("/find/password")
  public ResponseEntity<findPasswordResponse> findPassword(
      @RequestParam(name = "loginId") String loginId
  ) throws NoSuchAlgorithmException {
    log.info("비밀번호 찾기 요청");
    boolean success = userService.findPassword(loginId);
    log.info("비밀번호 찾기 성공");

    return ResponseEntity.ok(
        findPasswordResponse.builder().isFindPassword(success).build());
  }
}
