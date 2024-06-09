package com.zerobase.hoops.friends.controller;

import com.zerobase.hoops.friends.dto.FriendDto;
import com.zerobase.hoops.friends.dto.FriendDto.AcceptRequest;
import com.zerobase.hoops.friends.dto.FriendDto.AcceptResponse;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyResponse;
import com.zerobase.hoops.friends.dto.FriendDto.CancelRequest;
import com.zerobase.hoops.friends.dto.FriendDto.CancelResponse;
import com.zerobase.hoops.friends.dto.FriendDto.DeleteRequest;
import com.zerobase.hoops.friends.dto.FriendDto.DeleteResponse;
import com.zerobase.hoops.friends.dto.FriendDto.InviteFriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RejectRequest;
import com.zerobase.hoops.friends.dto.FriendDto.RejectResponse;
import com.zerobase.hoops.friends.dto.FriendDto.FriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RequestFriendListResponse;
import com.zerobase.hoops.friends.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendController {

  private final FriendService friendService;

  @Operation(summary = "친구 신청")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/apply")
  public ResponseEntity<ApplyResponse> applyFriend(
      @RequestBody @Validated FriendDto.ApplyRequest request) {
    ApplyResponse result = friendService.applyFriend(request);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 신청 취소")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/cancel")
  public ResponseEntity<CancelResponse> cancelFriend(
      @RequestBody @Validated CancelRequest request) {
    CancelResponse result = friendService.cancelFriend(request);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 수락")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/accept")
  public ResponseEntity<Map<String, List<AcceptResponse>>> acceptFriend(
      @RequestBody @Validated AcceptRequest request) {
    List<AcceptResponse> result = friendService.acceptFriend(request);
    return ResponseEntity.ok(Collections.singletonMap("friendList", result));
  }

  @Operation(summary = "친구 거절")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/reject")
  public ResponseEntity<RejectResponse> rejectFriend(
      @RequestBody @Validated RejectRequest request) {
    RejectResponse result = friendService.rejectFriend(request);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 삭제")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/delete")
  public ResponseEntity<Map<String, List<DeleteResponse>>> deleteFriend(
      @RequestBody @Validated DeleteRequest request) {
    List<DeleteResponse> result = friendService.deleteFriend(request);
    return ResponseEntity.ok(Collections.singletonMap("friendList", result));
  }

  @Operation(summary = "친구 검색")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/search")
  public ResponseEntity<Page<FriendListResponse>> searchFriend(
      @RequestParam String nickName,
      @PageableDefault(size = 10, page = 0) Pageable pageable) {
    Page<FriendListResponse> result = friendService.searchNickName(nickName,
        pageable);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 리스트 조회")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/myfriends")
  public ResponseEntity<Map<String, List<FriendListResponse>>> getMyFriends(
      @PageableDefault(size = 10, page = 0, sort = "FriendUserNickName",
          direction = Direction.ASC) Pageable pageable) {
    List<FriendListResponse> result = friendService.getMyFriends(pageable);
    return ResponseEntity.ok(Collections.singletonMap("myFriendList", result));
  }

  @Operation(summary = "경기 초대 친구 리스트 조회")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/invite/list")
  public ResponseEntity<Page<InviteFriendListResponse>> getMyInviteList(
      @RequestParam Long gameId,
      @PageableDefault(size = 10, page = 0) Pageable pageable) {
    Page<InviteFriendListResponse> result =
        friendService.getMyInviteList(gameId, pageable);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "내가 친구 요청 받은 리스트 조회")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/requestFriendList")
  public ResponseEntity<Map<String, List<RequestFriendListResponse>>> getRequestFriendList() {
    List<RequestFriendListResponse> result = friendService.getRequestFriendList();
    return ResponseEntity.ok(Collections.singletonMap("requestFriendList",
        result));
  }

}
