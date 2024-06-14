package com.zerobase.hoops.gameCreator.controller;

import com.zerobase.hoops.gameCreator.dto.ParticipantDto;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.ListResponse;
import com.zerobase.hoops.gameCreator.service.ParticipantGameService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game-creator/participant")
@RequiredArgsConstructor
public class ParticipantGameController {

  private final ParticipantGameService participantGameService;

  @Operation(summary = "경기 지원자 리스트 조회")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/apply/list")
  public ResponseEntity<Map<String, List<ListResponse>>>
  getApplyParticipantList(@RequestParam("gameId") Long gameId,
      @PageableDefault(page = 10, size = 0, sort = "createdDateTime",
          direction = Direction.ASC) Pageable pageable) {
    List<ListResponse> result =
        participantGameService.validApplyParticipantList(gameId, pageable);

    return ResponseEntity.ok(Collections.singletonMap(
        "applyParticipantGameList", result));
  }

  @Operation(summary = "경기 참가자 리스트 조회")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/accept/list")
  public ResponseEntity<Map<String, List<ListResponse>>>
  getAcceptParticipantList(@RequestParam("gameId") Long gameId,
      @PageableDefault(page = 10, size = 0, sort = "createdDateTime",
          direction = Direction.ASC) Pageable pageable) {
    List<ListResponse> result =
        participantGameService.validAcceptParticipantList(gameId, pageable);

    return ResponseEntity.ok(Collections.singletonMap(
        "acceptParticipantGameList", result));
  }

  @Operation(summary = "경기 지원자 수락")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/accept")
  public ResponseEntity<Map<String, String>>
  acceptParticipant(@RequestBody @Validated ParticipantDto.CommonRequest request) {
    String message = participantGameService.validAcceptParticipant(request);
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "경기 지원자 거절")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/reject")
  public ResponseEntity<Map<String, String>>
  rejectParticipant(@RequestBody @Validated ParticipantDto.CommonRequest request) {
    String message = participantGameService.validRejectParticipant(request);
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "경기 참가자 강퇴")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/kickout")
  public ResponseEntity<Map<String, String>>
  kickoutParticipant(@RequestBody @Validated ParticipantDto.CommonRequest request) {
    String message = participantGameService.validKickoutParticipant(request);
    return ResponseEntity.ok(Map.of("message", message));
  }


}
