package com.zerobase.hoops.invite.controller;

import com.zerobase.hoops.document.UserDocument;
import com.zerobase.hoops.invite.dto.AcceptInviteDto;
import com.zerobase.hoops.invite.dto.CancelInviteDto;
import com.zerobase.hoops.invite.dto.RejectInviteDto;
import com.zerobase.hoops.invite.dto.RequestInviteDto;
import com.zerobase.hoops.invite.dto.RequestInviteListDto;
import com.zerobase.hoops.invite.service.InviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/invite")
@RequiredArgsConstructor
@Tag(name = "INVITE", description = "초대 API")
public class InviteController {
  private final InviteService inviteService;

  @Operation(summary = "경기 초대 요청")
  @ApiResponse(responseCode = "200", description = "경기 초대 요청 성공",
      content = @Content(schema = @Schema(implementation = RequestInviteDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/request")
  public ResponseEntity<RequestInviteDto.Response> requestInvite(
      @RequestBody @Validated RequestInviteDto.Request request,
      @AuthenticationPrincipal UserDocument user) {
    log.info("loginId = {} requestInvite start", user.getLoginId());
    RequestInviteDto.Response result = inviteService.validRequestInvite
        (request, user);
    log.info("loginId = {} requestInvite end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 초대 요청 취소")
  @ApiResponse(responseCode = "200", description = "경기 초대 요청 취소 성공",
      content = @Content(schema = @Schema(implementation = CancelInviteDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/cancel")
  public ResponseEntity<CancelInviteDto.Response> cancelInvite(
      @RequestBody @Validated CancelInviteDto.Request request,
      @AuthenticationPrincipal UserDocument user) {
    log.info("loginId = {} cancelInvite start", user.getLoginId());
    CancelInviteDto.Response result = inviteService.validCancelInvite
        (request, user);
    log.info("loginId = {} cancelInvite end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 초대 요청 수락")
  @ApiResponse(responseCode = "200", description = "경기 초대 요청 수락 성공",
      content = @Content(schema = @Schema(implementation = AcceptInviteDto.Response.class),
      examples = {
          @ExampleObject(name = "경기 개설자가 초대", description = "메세지",
              value = "{\"message\":\"경기 개설자가 초대 했으므로 경기에 바로 참가합니다.\"}"),
          @ExampleObject(name = "참가자가 초대", description = "메세지",
              value = "{\"message\":\"참가자가 초대 했으므로 경기 개설자가 수락하면 경기에 참가할수 있습니다.\"}")
      }))
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/receive/accept")
  public ResponseEntity<AcceptInviteDto.Response> acceptInvite(
      @RequestBody @Validated AcceptInviteDto.Request request,
      @AuthenticationPrincipal UserDocument user) {
    log.info("loginId = {} acceptInvite start", user.getLoginId());
    AcceptInviteDto.Response result = inviteService.validAcceptInvite
        (request, user);
    log.info("loginId = {} acceptInvite end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 초대 요청 거절")
  @ApiResponse(responseCode = "200", description = "경기 초대 요청 취소 성공",
      content = @Content(schema = @Schema(implementation = RejectInviteDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/receive/reject")
  public ResponseEntity<RejectInviteDto.Response> rejectInvite(
      @RequestBody @Validated RejectInviteDto.Request request,
      @AuthenticationPrincipal UserDocument user) {
    log.info("loginId = {} rejectInvite start", user.getLoginId());
    RejectInviteDto.Response result = inviteService.validRejectInvite
        (request, user);
    log.info("loginId = {} rejectInvite end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "내가 초대 요청 받은 리스트 조회")
  @ApiResponse(responseCode = "200", description = "내가 초대 요청 받은 리스트 조회 성공",
      content = @Content(schema = @Schema(implementation = RequestInviteListDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/myList")
  public ResponseEntity<Map<String, List<RequestInviteListDto.Response>>> getInviteRequestList(
      @Parameter(name = "page", description = "페이지 번호", example = "0",
          required = true)
      @RequestParam(defaultValue = "0") int page,
      @Parameter(name = "size", description = "페이지 크기", example = "10",
          required = true)
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal UserDocument user
  ) {

    log.info("loginId = {} getInviteRequestList start", user.getLoginId());
    Pageable pageable = PageRequest.of(page, size, Direction.ASC, "requestedDateTime");
    List<RequestInviteListDto.Response> result =
        inviteService.validGetRequestInviteList(pageable, user);
    log.info("loginId = {} getInviteRequestList end", user.getLoginId());

    return ResponseEntity.ok(Map.of("inviteList", result));
  }

}
