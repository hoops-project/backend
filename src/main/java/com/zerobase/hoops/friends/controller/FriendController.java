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
import com.zerobase.hoops.friends.dto.FriendDto.ListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RequestListResponse;
import com.zerobase.hoops.friends.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

  @Operation(summary = "친구 신청")
  @PostMapping("/apply")
  public ResponseEntity<ApplyResponse> applyFriend(
      @RequestBody @Validated FriendDto.ApplyRequest request) {
    ApplyResponse result = friendService.applyFriend(request);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 신청 취소")
  @PatchMapping("/cancel")
  public ResponseEntity<CancelResponse> cancelFriend(
      @RequestBody @Validated CancelRequest request) {
    CancelResponse result = friendService.cancelFriend(request);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 수락")
  @PatchMapping("/accept")
  public ResponseEntity<Map<String, List<AcceptResponse>>> acceptFriend(
      @RequestBody @Validated AcceptRequest request) {
    List<AcceptResponse> result = friendService.acceptFriend(request);
    return ResponseEntity.ok(Collections.singletonMap("friendList", result));
  }

  @Operation(summary = "친구 거절")
  @PatchMapping("/reject")
  public ResponseEntity<RejectResponse> rejectFriend(
      @RequestBody @Validated RejectRequest request) {
    RejectResponse result = friendService.rejectFriend(request);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 삭제")
  @PatchMapping("/delete")
  public ResponseEntity<Map<String, List<DeleteResponse>>> deleteFriend(
      @RequestBody @Validated DeleteRequest request) {
    List<DeleteResponse> result = friendService.deleteFriend(request);
    return ResponseEntity.ok(Collections.singletonMap("friendList", result));
  }

  @Operation(summary = "친구 검색")
  @GetMapping("/search")
  public ResponseEntity<Page<ListResponse>> searchFriend(
      @RequestParam String nickName,
      @PageableDefault(size = 10, page = 0) Pageable pageable) {
    Page<ListResponse> result = friendService.searchNickName(nickName,
        pageable);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 리스트 조회")
  @GetMapping("/myfriends")
  public ResponseEntity<Map<String, List<ListResponse>>> getMyFriends(
      @PageableDefault(size = 10, page = 0, sort = "FriendUserEntityNickName",
          direction = Direction.ASC) Pageable pageable) {
    List<ListResponse> result = friendService.getMyFriends(pageable);
    return ResponseEntity.ok(Collections.singletonMap("myFriendList", result));
  }

  @Operation(summary = "내가 친구 요청 받은 리스트 조회")
  @GetMapping("/requestFriendList")
  public ResponseEntity<Map<String, List<RequestListResponse>>> getRequestFriendList() {
    List<RequestListResponse> result = friendService.getRequestFriendList();
    return ResponseEntity.ok(Collections.singletonMap("requestFriendList",
        result));
  }

}
