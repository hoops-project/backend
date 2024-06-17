package com.zerobase.hoops.invite.controller;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.invite.dto.InviteDto.CommonRequest;
import com.zerobase.hoops.invite.dto.InviteDto.CreateRequest;
import com.zerobase.hoops.invite.dto.InviteDto.InviteMyListResponse;
import com.zerobase.hoops.invite.service.InviteService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/invite")
@RequiredArgsConstructor
public class InviteController {
  private final InviteService inviteService;

  @Operation(summary = "경기 초대 요청")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/request")
  public ResponseEntity<Map<String, String>> requestInvite(
      @RequestBody @Validated CreateRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} requestInvite start", user.getLoginId());
    String message = inviteService.validRequestInvite(request, user);
    log.info("loginId = {} requestInvite end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "경기 초대 요청 취소")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/cancel")
  public ResponseEntity<Map<String, String>> cancelInvite(
      @RequestBody @Validated CommonRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} cancelInvite start", user.getLoginId());
    String message = inviteService.validCancelInvite(request, user);
    log.info("loginId = {} cancelInvite end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "경기 초대 요청 수락")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/receive/accept")
  public ResponseEntity<Map<String, String>> acceptInvite(
      @RequestBody @Validated CommonRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} acceptInvite start", user.getLoginId());
    String message = inviteService.validAcceptInvite(request, user);
    log.info("loginId = {} acceptInvite end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "경기 초대 요청 거절")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/receive/reject")
  public ResponseEntity<Map<String, String>> rejectInvite(
      @RequestBody @Validated CommonRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} rejectInvite start", user.getLoginId());
    String message = inviteService.validRejectInvite(request, user);
    log.info("loginId = {} rejectInvite end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "내가 초대 요청 받은 리스트 조회")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/myList")
  public ResponseEntity<Map<String, List<InviteMyListResponse>>> getInviteRequestList(
      @PageableDefault(page = 0, size = 10,
          sort = "requestedDateTime", direction = Direction.ASC) Pageable pageable,
      @AuthenticationPrincipal UserEntity user
  ) {
    log.info("loginId = {} getInviteRequestList start", user.getLoginId());
    List<InviteMyListResponse> result =
        inviteService.validGetRequestInviteList(pageable, user);
    log.info("loginId = {} getInviteRequestList end", user.getLoginId());
    return ResponseEntity.ok(Map.of("inviteList", result));
  }

}
