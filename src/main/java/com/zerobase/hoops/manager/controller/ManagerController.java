package com.zerobase.hoops.manager.controller;

import com.zerobase.hoops.commonResponse.ApiResponse;
import com.zerobase.hoops.manager.dto.BlackListDto;
import com.zerobase.hoops.manager.dto.UnLockBlackListDto;
import com.zerobase.hoops.manager.service.ManagerService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "6. BlackList")
public class ManagerController {

  private final ManagerService managerService;

  @PreAuthorize("hasRole('OWNER')")
  @PostMapping("/black-list")
  public ResponseEntity<ApiResponse> saveBlackList(
      @RequestBody BlackListDto request) {
    managerService.saveBlackList(request);
    return ResponseEntity.ok().body(
        new ApiResponse("블랙리스트", "Success"));
  }

  @PreAuthorize("hasRole('OWNER')")
  @GetMapping("/black-list")
  public ResponseEntity<ApiResponse> getBlackList(
      @RequestParam String loginId) {
    managerService.getBlackList(loginId);
    return ResponseEntity.ok().body(
        new ApiResponse("블랙리스트", "true"));
  }

  @PreAuthorize("hasRole('OWNER')")
  @PatchMapping("/unlock-black-list")
  public ResponseEntity<?> blackTest(
      @RequestBody UnLockBlackListDto request) {
    managerService.unLockBlackList(request);
    return ResponseEntity.ok().body(
        new ApiResponse("블랙리스트 해제", "Success"));
  }

}
