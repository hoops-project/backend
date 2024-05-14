package com.zerobase.hoops.invite.controller;


import com.zerobase.hoops.invite.dto.InviteDto;
import com.zerobase.hoops.invite.dto.InviteDto.CancelResponse;
import com.zerobase.hoops.invite.dto.InviteDto.CreateResponse;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveAcceptResponse;
import com.zerobase.hoops.invite.dto.InviteDto.ReceiveRejectResponse;
import com.zerobase.hoops.invite.service.InviteService;
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
@RequestMapping("/api/invite")
@RequiredArgsConstructor
public class InviteController {
  private final InviteService inviteService;

  @Operation(summary = "경기 초대 요청")
  @PostMapping("/request")
  public ResponseEntity<CreateResponse> requestInviteGame(
      @RequestBody @Validated InviteDto.CreateRequest request) {
    CreateResponse result = inviteService.requestInviteGame(request);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 초대 요청 취소")
  @PatchMapping("/cancel")
  public ResponseEntity<CancelResponse> cancelInviteGame(
      @RequestBody @Validated InviteDto.CancelRequest request) {
    CancelResponse result = inviteService.cancelInviteGame(request);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 초대 요청 상대방 수락")
  @PatchMapping("/receive/accept")
  public ResponseEntity<ReceiveAcceptResponse> receiveAcceptInviteGame(
      @RequestBody @Validated InviteDto.ReceiveAcceptRequest request) {
    ReceiveAcceptResponse result = inviteService.receiveAcceptInviteGame(request);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 초대 요청 상대방 거절")
  @PatchMapping("/receive/reject")
  public ResponseEntity<ReceiveRejectResponse> receiveRejectInviteGame(
      @RequestBody @Validated InviteDto.ReceiveRejectRequest request) {
    ReceiveRejectResponse result =
        inviteService.receiveRejectInviteGame(request);
    return ResponseEntity.ok(result);
  }

}
