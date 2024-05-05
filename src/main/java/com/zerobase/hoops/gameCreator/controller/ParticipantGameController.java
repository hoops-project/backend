package com.zerobase.hoops.gameCreator.controller;

import com.zerobase.hoops.gameCreator.dto.ParticipantDto;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.DetailResponse;
import com.zerobase.hoops.gameCreator.service.ParticipantGameService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game-creator")
@RequiredArgsConstructor
public class ParticipantGameController {

  private final ParticipantGameService participantGameService;

  /**
   * 경기 참가자 리스트 조회
   */
  @Operation(summary = "경기 참가자 리스트 조회")
  @GetMapping("/participant/list")
  public ResponseEntity<Map<String, List<DetailResponse>>>
  getParticipantList(@RequestParam("gameId") Long gameId) {
    List<ParticipantDto.DetailResponse> result =
        participantGameService.getParticipantList(gameId);

    return ResponseEntity.ok(Collections.singletonMap("participantGameList",
        result));
  }

}
