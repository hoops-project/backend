package com.zerobase.hoops.gameCreator.controller;

import com.zerobase.hoops.gameCreator.dto.ParticipantDto;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.DetailResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
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
  @GetMapping("/list")
  public ResponseEntity<Map<String, List<DetailResponse>>>
  getParticipantList(@RequestParam("gameId") Long gameId,
      @RequestHeader("Authorization") String token) {
    List<ParticipantDto.DetailResponse> result =
        participantGameService.getParticipantList(gameId, token);

    return ResponseEntity.ok(Collections.singletonMap("participantGameList",
        result));
  }

  /**
   * 경기 참가 희망자 수락
   */
  @Operation(summary = "경기 참가 희망자 수락")
  @PostMapping("/accept")
  public ResponseEntity<Map<String, String>>
  acceptParticipant(@RequestParam @Validated ParticipantDto.AcceptRequest request,
      @RequestHeader("Authorization") String token) {
    participantGameService.acceptParticipant(request, token);

    Map<String, String> result = new HashMap<>();
    result.put("data", "SUCCESS");

    return ResponseEntity.ok(result);
  }

  /**
   * 경기 참가 희망자 거절
   */
  @Operation(summary = "경기 참가 희망자 거절")
  @PostMapping("/reject")
  public ResponseEntity<Map<String, String>>
  rejectParticipant(@RequestParam @Validated ParticipantDto.RejectRequest request,
      @RequestHeader("Authorization") String token) {
    participantGameService.rejectParticipant(request, token);

    Map<String, String> result = new HashMap<>();
    result.put("data", "SUCCESS");

    return ResponseEntity.ok(result);
  }

  /**
   * 경기 참가자 강퇴
   */
  @Operation(summary = "경기 참가자 강퇴")
  @PostMapping("/kickout")
  public ResponseEntity<Map<String, String>>
  kickoutParticipant(@RequestParam @Validated ParticipantDto.KickoutRequest request,
      @RequestHeader("Authorization") String token) {
    participantGameService.kickoutParticipant(request, token);

    Map<String, String> result = new HashMap<>();
    result.put("data", "SUCCESS");

    return ResponseEntity.ok(result);
  }


}
