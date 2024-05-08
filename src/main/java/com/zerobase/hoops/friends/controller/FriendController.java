package com.zerobase.hoops.friends.controller;

import com.zerobase.hoops.friends.dto.FriendDto;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyResponse;
import com.zerobase.hoops.friends.dto.FriendDto.CancelResponse;
import com.zerobase.hoops.friends.service.FriendService;
import com.zerobase.hoops.gameCreator.dto.GameDto;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateResponse;
import com.zerobase.hoops.gameCreator.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    FriendDto.ApplyResponse result = friendService.applyFriend(request);
    return ResponseEntity.ok(result);
  }

  /**
   * 친구 신청 취소
   */
  @Operation(summary = "친구 신청 취소")
  @PatchMapping("/cancel")
  public ResponseEntity<CancelResponse> cancelFriend(
      @RequestBody @Validated FriendDto.CancelRequest request) {
    FriendDto.CancelResponse result = friendService.cancelFriend(request);
    return ResponseEntity.ok(result);
  }

}
