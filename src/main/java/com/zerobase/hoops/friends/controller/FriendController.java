package com.zerobase.hoops.friends.controller;

import com.zerobase.hoops.friends.dto.FriendDto;
import com.zerobase.hoops.friends.dto.FriendDto.AcceptRequest;
import com.zerobase.hoops.friends.dto.FriendDto.AcceptResponse;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyResponse;
import com.zerobase.hoops.friends.dto.FriendDto.CancelRequest;
import com.zerobase.hoops.friends.dto.FriendDto.CancelResponse;
import com.zerobase.hoops.friends.dto.FriendDto.DeleteRequest;
import com.zerobase.hoops.friends.dto.FriendDto.DeleteResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RejectRequest;
import com.zerobase.hoops.friends.dto.FriendDto.RejectResponse;
import com.zerobase.hoops.friends.dto.FriendDto.SearchResponse;
import com.zerobase.hoops.friends.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  /**
   * 친구 신청
   */
  @Operation(summary = "친구 신청")
  @PostMapping("/apply")
  public ResponseEntity<ApplyResponse> applyFriend(
      @RequestBody @Validated FriendDto.ApplyRequest request) {
    ApplyResponse result = friendService.applyFriend(request);
    return ResponseEntity.ok(result);
  }

  /**
   * 친구 신청 취소
   */
  @Operation(summary = "친구 신청 취소")
  @PatchMapping("/cancel")
  public ResponseEntity<CancelResponse> cancelFriend(
      @RequestBody @Validated CancelRequest request) {
    CancelResponse result = friendService.cancelFriend(request);
    return ResponseEntity.ok(result);
  }

  /**
   * 친구 수락
   */
  @Operation(summary = "친구 수락")
  @PatchMapping("/accept")
  public ResponseEntity<Map<String, List<AcceptResponse>>> acceptFriend(
      @RequestBody @Validated AcceptRequest request) {
    List<AcceptResponse> result = friendService.acceptFriend(request);
    return ResponseEntity.ok(Collections.singletonMap("friendList", result));
  }

  /**
   * 친구 거절
   */
  @Operation(summary = "친구 거절")
  @PatchMapping("/reject")
  public ResponseEntity<RejectResponse> rejectFriend(
      @RequestBody @Validated RejectRequest request) {
    RejectResponse result = friendService.rejectFriend(request);
    return ResponseEntity.ok(result);
  }

  /**
   * 친구 삭제
   */
  @Operation(summary = "친구 삭제")
  @PatchMapping("/delete")
  public ResponseEntity<Map<String, List<DeleteResponse>>> deleteFriend(
      @RequestBody @Validated DeleteRequest request) {
    List<DeleteResponse> result = friendService.deleteFriend(request);
    return ResponseEntity.ok(Collections.singletonMap("friendList", result));
  }

  /**
   * 친구 검색
   */
  @Operation(summary = "친구 검색")
  @GetMapping("/search")
  public ResponseEntity<Map<String, List<SearchResponse>>> searchFriend(
      @RequestParam String nickName) {
    List<SearchResponse> result = friendService.searchNickName(nickName);
    return ResponseEntity.ok(Collections.singletonMap("searchList", result));
  }

}
