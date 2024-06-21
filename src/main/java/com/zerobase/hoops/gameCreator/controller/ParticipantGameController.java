package com.zerobase.hoops.gameCreator.controller;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.dto.AcceptParticipantDto;
import com.zerobase.hoops.gameCreator.dto.ApplyParticipantListDto;
import com.zerobase.hoops.gameCreator.dto.AcceptParticipantListDto;
import com.zerobase.hoops.gameCreator.dto.KickoutParticipantDto;
import com.zerobase.hoops.gameCreator.dto.RejectParticipantDto;
import com.zerobase.hoops.gameCreator.service.ParticipantGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
  @ApiResponse(responseCode = "200", description = "경기 지원자 리스트 조회 성공",
      content = @Content(schema = @Schema(implementation = ApplyParticipantListDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/apply/list")
  public ResponseEntity<Map<String, List<ApplyParticipantListDto.Response>>> getApplyParticipantList(
      @RequestParam("gameId")
      @Parameter(name = "gameId", description = "경기 아이디",
          example = "1", required = true) Long gameId,
      @PageableDefault(page = 10, size = 0, sort = "createdDateTime",
          direction = Direction.ASC) Pageable pageable,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} getApplyParticipantList start", user.getLoginId());
    List<ApplyParticipantListDto.Response> result =
        participantGameService.validApplyParticipantList(gameId, pageable, user);

    log.info("loginId = {} getApplyParticipantList end", user.getLoginId());
    return ResponseEntity.ok(Collections.singletonMap(
        "applyParticipantGameList", result));
  }

  @Operation(summary = "경기 참가자 리스트 조회")
  @ApiResponse(responseCode = "200", description = "경기 참가자 리스트 조회 성공",
      content = @Content(schema = @Schema(implementation = AcceptParticipantListDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/accept/list")
  public ResponseEntity<Map<String, List<AcceptParticipantListDto.Response>>> getAcceptParticipantList(
      @RequestParam("gameId")
      @Parameter(name = "gameId", description = "경기 아이디",
          example = "1", required = true) Long gameId,
      @PageableDefault(page = 10, size = 0, sort = "createdDateTime",
          direction = Direction.ASC) Pageable pageable,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} getAcceptParticipantList start", user.getLoginId());
    List<AcceptParticipantListDto.Response> result =
        participantGameService.validAcceptParticipantList(gameId, pageable, user);

    log.info("loginId = {} getAcceptParticipantList end", user.getLoginId());
    return ResponseEntity.ok(Collections.singletonMap(
        "acceptParticipantGameList", result));
  }

  @Operation(summary = "경기 지원자 수락")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/accept")
  public ResponseEntity<AcceptParticipantDto.Response> acceptParticipant(
      @RequestBody @Validated AcceptParticipantDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} acceptParticipant start", user.getLoginId());
    AcceptParticipantDto.Response result =
        participantGameService.validAcceptParticipant(request, user);
    log.info("loginId = {} acceptParticipant end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 지원자 거절")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/reject")
  public ResponseEntity<RejectParticipantDto.Response> rejectParticipant(
      @RequestBody @Validated RejectParticipantDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} rejectParticipant start", user.getLoginId());
    RejectParticipantDto.Response result =
        participantGameService.validRejectParticipant(request, user);
    log.info("loginId = {} rejectParticipant end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 참가자 강퇴")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/kickout")
  public ResponseEntity<KickoutParticipantDto.Response> kickoutParticipant(
      @RequestBody @Validated KickoutParticipantDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} kickoutParticipant start", user.getLoginId());
    KickoutParticipantDto.Response result =
        participantGameService.validKickoutParticipant(request, user);
    log.info("loginId = {} kickoutParticipant end", user.getLoginId());
    return ResponseEntity.ok(result);
  }


}
