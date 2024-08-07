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
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
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
@Tag(name = "PARTICIPANT", description = "경기 참여 API")
public class ParticipantGameController {

  private final ParticipantGameService participantGameService;

  @Operation(summary = "경기 지원자 리스트 조회")
  @ApiResponse(responseCode = "200", description = "경기 지원자 리스트 조회 성공",
      content = @Content(schema = @Schema(implementation = ApplyParticipantListDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/apply/list")
  public ResponseEntity<Map<String, List<ApplyParticipantListDto.Response>>> getApplyParticipantList(
      @RequestParam("gameId")
      @Parameter(name = "gameId", description = "경기 pk",
          example = "1", required = true) Long gameId,
      @Parameter(name = "page", description = "페이지 번호", example = "0",
          required = true)
      @RequestParam(defaultValue = "0") int page,
      @Parameter(name = "size", description = "페이지 크기", example = "10",
          required = true)
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} getApplyParticipantList start", user.getLoginId());
    Pageable pageable = PageRequest.of(page, size,
        Direction.ASC, "createdDateTime");
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
      @Parameter(name = "gameId", description = "경기 pk",
          example = "1", required = true) Long gameId,
      @Parameter(name = "page", description = "페이지 번호", example = "0",
          required = true)
      @RequestParam(defaultValue = "0") int page,
      @Parameter(name = "size", description = "페이지 크기", example = "10",
          required = true)
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} getAcceptParticipantList start", user.getLoginId());
    Pageable pageable = PageRequest.of(page, size,
        Direction.ASC, "createdDateTime");
    List<AcceptParticipantListDto.Response> result =
        participantGameService.validAcceptParticipantList(gameId, pageable, user);

    log.info("loginId = {} getAcceptParticipantList end", user.getLoginId());
    return ResponseEntity.ok(Collections.singletonMap(
        "acceptParticipantGameList", result));
  }

  @Operation(summary = "경기 지원자 수락")
  @ApiResponse(responseCode = "200", description = "경기 참가자 리스트 조회 성공",
      content = @Content(schema = @Schema(implementation = AcceptParticipantDto.Response.class)))
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
  @ApiResponse(responseCode = "200", description = "경기 참가자 리스트 조회 성공",
      content = @Content(schema = @Schema(implementation = RejectParticipantDto.Response.class)))
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
  @ApiResponse(responseCode = "200", description = "경기 참가자 리스트 조회 성공",
      content = @Content(schema = @Schema(implementation = KickoutParticipantDto.Response.class)))
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
