package com.zerobase.hoops.manager.controller;

import com.zerobase.hoops.commonResponse.ApiResponseFactory;
import com.zerobase.hoops.commonResponse.BasicApiResponse;
import com.zerobase.hoops.commonResponse.swaggerSchema.BlackListResponse;
import com.zerobase.hoops.manager.dto.BlackListDto;
import com.zerobase.hoops.manager.dto.UnLockBlackListDto;
import com.zerobase.hoops.manager.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@Tag(name = "MANAGER")
public class ManagerController {

  private final ManagerService managerService;
  private final ApiResponseFactory apiResponseFactory;

  @Operation(summary = "블랙리스트 적용")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "블랙리스트 적용 성공",
          content = @Content(schema = @Schema(implementation = BlackListResponse.BlackListSuccess.class))),
  })
  @Parameter(name = "reportedId", description = "신고당하는 유저의 pk", required = true)
  @PreAuthorize("hasRole('OWNER')")
  @PostMapping("/black-list")
  public ResponseEntity<BasicApiResponse> saveBlackList(
      @RequestBody @Valid BlackListDto request) {
    managerService.saveBlackList(request);
    return ResponseEntity.ok().body(
        apiResponseFactory.createSuccessResponse("블랙리스트"));
  }

  @Operation(summary = "블랙리스트 여부 체크")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "블랙리스트 여부 체크 - True / False",
          content = @Content(schema = @Schema(implementation = BlackListResponse.BlackUserTrueFalse.class))),
  })
  @PreAuthorize("hasRole('OWNER')")
  @GetMapping("/black-list")
  public ResponseEntity<BasicApiResponse> getBlackList(
      @Parameter(description = "블랙리스트 유저의 로그인 아이디", name = "loginId")
      @RequestParam String loginId) {
    managerService.getBlackList(loginId);
    return ResponseEntity.ok().body(
        apiResponseFactory.createSuccessWithDetailResponse(
            "블랙리스트", "true"));
  }

  @Operation(summary = "블랙리스트 해제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "블랙리스트 여부 체크 - True / False",
          content = @Content(schema = @Schema(implementation = BlackListResponse.BlackUserUnlock.class))),
  })
  @Parameter(name = "blackUserId", description = "블랙 유저의 로그인 ID", required = true)
  @PreAuthorize("hasRole('OWNER')")
  @PatchMapping("/unlock-black-list")
  public ResponseEntity<BasicApiResponse> blackTest(
      @RequestBody UnLockBlackListDto request) {
    managerService.unLockBlackList(request);
    return ResponseEntity.ok().body(
        apiResponseFactory.createSuccessResponse("블랙리스트"));
  }

}
