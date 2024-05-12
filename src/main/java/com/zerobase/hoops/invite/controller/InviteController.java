package com.zerobase.hoops.invite.controller;


import com.zerobase.hoops.gameCreator.service.GameService;
import com.zerobase.hoops.invite.dto.InviteDto;
import com.zerobase.hoops.invite.dto.InviteDto.CreateResponse;
import com.zerobase.hoops.invite.service.InviteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invite")
@RequiredArgsConstructor
public class InviteController {
  private final InviteService inviteService;

  /**
   * 경기 초대 요청
   */
  @Operation(summary = "경기 초대 요청")
  @PostMapping("/request")
  public ResponseEntity<CreateResponse> requestInviteGame(
      @RequestBody @Validated InviteDto.CreateRequest request) {
    InviteDto.CreateResponse result = inviteService.requestInviteGame(request);
    return ResponseEntity.ok(result);
  }

}
