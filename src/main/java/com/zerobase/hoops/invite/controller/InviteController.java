package com.zerobase.hoops.invite.controller;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.invite.dto.CommonInviteDto;
import com.zerobase.hoops.invite.dto.RequestInviteDto;
import com.zerobase.hoops.invite.dto.RequestInviteListDto;
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
  public ResponseEntity<CommonInviteDto.Response> requestInvite(
      @RequestBody @Validated RequestInviteDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} requestInvite start", user.getLoginId());
    CommonInviteDto.Response result = inviteService.validRequestInvite
        (request, user);
    log.info("loginId = {} requestInvite end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 초대 요청 취소")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/cancel")
  public ResponseEntity<CommonInviteDto.Response> cancelInvite(
      @RequestBody @Validated CommonInviteDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} cancelInvite start", user.getLoginId());
    CommonInviteDto.Response result = inviteService.validCancelInvite
        (request, user);
    log.info("loginId = {} cancelInvite end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 초대 요청 수락")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/receive/accept")
  public ResponseEntity<CommonInviteDto.Response> acceptInvite(
      @RequestBody @Validated CommonInviteDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} acceptInvite start", user.getLoginId());
    CommonInviteDto.Response result = inviteService.validAcceptInvite
        (request, user);
    log.info("loginId = {} acceptInvite end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 초대 요청 거절")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/receive/reject")
  public ResponseEntity<CommonInviteDto.Response> rejectInvite(
      @RequestBody @Validated CommonInviteDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} rejectInvite start", user.getLoginId());
    CommonInviteDto.Response result = inviteService.validRejectInvite
        (request, user);
    log.info("loginId = {} rejectInvite end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "내가 초대 요청 받은 리스트 조회")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/myList")
  public ResponseEntity<Map<String, List<RequestInviteListDto.Response>>> getInviteRequestList(
      @PageableDefault(page = 0, size = 10,
          sort = "requestedDateTime", direction = Direction.ASC) Pageable pageable,
      @AuthenticationPrincipal UserEntity user
  ) {
    log.info("loginId = {} getInviteRequestList start", user.getLoginId());
    List<RequestInviteListDto.Response> result =
        inviteService.validGetRequestInviteList(pageable, user);
    log.info("loginId = {} getInviteRequestList end", user.getLoginId());
    return ResponseEntity.ok(Map.of("inviteList", result));
  }

}
