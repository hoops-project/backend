package com.zerobase.hoops.gameCreator.controller;

import com.zerobase.hoops.gameCreator.dto.ParticipantDto;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.AcceptResponse;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.KickoutResponse;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.RejectResponse;
import com.zerobase.hoops.gameCreator.service.ParticipantGameService;
import com.zerobase.hoops.gameCreator.type.ParticipantGameStatus;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game-creator/participant")
@RequiredArgsConstructor
public class ParticipantGameController {

  private final ParticipantGameService participantGameService;

  /**
   * 경기 참가 희망자 리스트 조회
   */
  @Operation(summary = "경기 참가 희망자 리스트 조회")
  @GetMapping("/apply/list")
  public ResponseEntity<Map<String, List<DetailResponse>>>
  getParticipantList(@RequestParam("gameId") Long gameId) {
    List<ParticipantDto.DetailResponse> result =
        participantGameService.getParticipantList(gameId);

    return ResponseEntity.ok(Collections.singletonMap("participantGameList",
        result));
  }

  /**
   * 경기 참가 희망자 수락
   */
  @Operation(summary = "경기 참가 희망자 수락")
  @PatchMapping("/accept")
  public ResponseEntity<AcceptResponse>
  acceptParticipant(@RequestBody @Validated ParticipantDto.AcceptRequest request) {
    AcceptResponse result = participantGameService.acceptParticipant(request);
    return ResponseEntity.ok(result);
  }

  /**
   * 경기 참가 희망자 거절
   */
  @Operation(summary = "경기 참가 희망자 거절")
  @PatchMapping("/reject")
  public ResponseEntity<RejectResponse>
  rejectParticipant(@RequestBody @Validated ParticipantDto.RejectRequest request) {
    RejectResponse result = participantGameService.rejectParticipant(request);
    return ResponseEntity.ok(result);
  }

  /**
   * 경기 참가자 강퇴
   */
  @Operation(summary = "경기 참가자 강퇴")
  @PatchMapping("/kickout")
  public ResponseEntity<KickoutResponse>
  kickoutParticipant(@RequestBody @Validated ParticipantDto.KickoutRequest request) {
    KickoutResponse result = participantGameService.kickoutParticipant(request);
    return ResponseEntity.ok(result);
  }


}
