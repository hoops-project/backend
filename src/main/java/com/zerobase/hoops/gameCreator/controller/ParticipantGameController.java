package com.zerobase.hoops.gameCreator.controller;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.ListResponse;
import com.zerobase.hoops.gameCreator.service.ParticipantGameService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
          direction = Direction.ASC) Pageable pageable,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} getApplyParticipantList start", user.getLoginId());
    List<ListResponse> result =
        participantGameService.validApplyParticipantList(gameId, pageable, user);

    log.info("loginId = {} getApplyParticipantList end", user.getLoginId());
    return ResponseEntity.ok(Collections.singletonMap(
        "applyParticipantGameList", result));
  }

  @Operation(summary = "경기 참가자 리스트 조회")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/accept/list")
  public ResponseEntity<Map<String, List<ListResponse>>>
  getAcceptParticipantList(@RequestParam("gameId") Long gameId,
      @PageableDefault(page = 10, size = 0, sort = "createdDateTime",
          direction = Direction.ASC) Pageable pageable,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} getAcceptParticipantList start", user.getLoginId());
    List<ListResponse> result =
        participantGameService.validAcceptParticipantList(gameId, pageable, user);

    log.info("loginId = {} getAcceptParticipantList end", user.getLoginId());
    return ResponseEntity.ok(Collections.singletonMap(
        "acceptParticipantGameList", result));
  }

  @Operation(summary = "경기 지원자 수락")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/accept")
  public ResponseEntity<Map<String, String>>
  acceptParticipant(@RequestBody @Validated ParticipantDto.CommonRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} acceptParticipant start", user.getLoginId());
    String message = participantGameService.validAcceptParticipant(request,
        user);
    log.info("loginId = {} acceptParticipant end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "경기 지원자 거절")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/reject")
  public ResponseEntity<Map<String, String>>
  rejectParticipant(@RequestBody @Validated ParticipantDto.CommonRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} rejectParticipant start", user.getLoginId());
    String message = participantGameService.validRejectParticipant(request,
        user);
    log.info("loginId = {} rejectParticipant end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  @Operation(summary = "경기 참가자 강퇴")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/kickout")
  public ResponseEntity<Map<String, String>>
  kickoutParticipant(@RequestBody @Validated ParticipantDto.CommonRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} kickoutParticipant start", user.getLoginId());
    String message = participantGameService.validKickoutParticipant(request,
        user);
    log.info("loginId = {} kickoutParticipant end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }


}
