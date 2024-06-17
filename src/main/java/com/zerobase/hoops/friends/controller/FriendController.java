package com.zerobase.hoops.friends.controller;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyRequest;
import com.zerobase.hoops.friends.dto.FriendDto.CommonRequest;
import com.zerobase.hoops.friends.dto.FriendDto.InviteFriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.FriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RequestFriendListResponse;
import com.zerobase.hoops.friends.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendController {

  private final FriendService friendService;

  @Operation(summary = "친구 신청")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/apply")
  public ResponseEntity<Map<String, String>> applyFriend(
      @RequestBody @Validated ApplyRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} applyFriend start", user.getLoginId());
    String message = friendService.validApplyFriend(request, user);
    log.info("loginId = {} applyFriend end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "친구 신청 취소")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/cancel")
  public ResponseEntity<Map<String, String>> cancelFriend(
      @RequestBody @Validated CommonRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} cancelFriend start", user.getLoginId());
    String message = friendService.validCancelFriend(request, user);
    log.info("loginId = {} cancelFriend end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "친구 수락")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/accept")
  public ResponseEntity<Map<String, String>> acceptFriend(
      @RequestBody @Validated CommonRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} acceptFriend start", user.getLoginId());
    String message = friendService.validAcceptFriend(request, user);
    log.info("loginId = {} acceptFriend end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "친구 거절")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/reject")
  public ResponseEntity<Map<String, String>> rejectFriend(
      @RequestBody @Validated CommonRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} rejectFriend start", user.getLoginId());
    String message = friendService.validRejectFriend(request, user);
    log.info("loginId = {} rejectFriend end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "친구 삭제")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/delete")
  public ResponseEntity<Map<String, String>> deleteFriend(
      @RequestBody @Validated CommonRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} deleteFriend start", user.getLoginId());
    String message = friendService.validDeleteFriend(request, user);
    log.info("loginId = {} deleteFriend end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "친구 검색")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/search")
  public ResponseEntity<Page<FriendListResponse>> searchFriend(
      @RequestParam String nickName,
      @PageableDefault(page = 0, size = 10) Pageable pageable,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} searchFriend start", user.getLoginId());
    Page<FriendListResponse> result =
        friendService.validSearchFriend(nickName, pageable, user);
    log.info("loginId = {} searchFriend end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 리스트 조회")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/myfriends")
  public ResponseEntity<Map<String, List<FriendListResponse>>> getMyFriendList(
      @PageableDefault(page = 0, size = 10, sort = "FriendUserNickName",
          direction = Direction.ASC) Pageable pageable,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} getMyFriendList start", user.getLoginId());
    List<FriendListResponse> result =
        friendService.validGetMyFriendList(pageable, user);
    log.info("loginId = {} getMyFriendList end", user.getLoginId());
    return ResponseEntity.ok(Map.of("myFriendList", result));
  }

  @Operation(summary = "경기 초대 친구 리스트 조회")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/invite/list")
  public ResponseEntity<Page<InviteFriendListResponse>> getMyInviteFriendList(
      @RequestParam Long gameId,
      @PageableDefault(page = 0, size = 10) Pageable pageable,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} getMyInviteFriendList start", user.getLoginId());
    Page<InviteFriendListResponse> result =
        friendService.validGetMyInviteFriendList(gameId, pageable, user);
    log.info("loginId = {} getMyInviteFriendList end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "내가 친구 요청 받은 리스트 조회")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/requestFriendList")
  public ResponseEntity<Map<String, List<RequestFriendListResponse>>> getRequestFriendList(
      @PageableDefault(page = 0, size = 10,
          sort = "createdDateTime", direction = Direction.ASC) Pageable pageable,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} getRequestFriendList start", user.getLoginId());
    List<RequestFriendListResponse> result =
        friendService.validGetRequestFriendList(pageable, user);
    log.info("loginId = {} getRequestFriendList end", user.getLoginId());
    return ResponseEntity.ok(Map.of("requestFriendList", result));
  }

}
