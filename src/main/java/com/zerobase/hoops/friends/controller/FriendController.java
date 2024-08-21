package com.zerobase.hoops.friends.controller;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.dto.AcceptFriendDto;
import com.zerobase.hoops.friends.dto.ApplyFriendDto;
import com.zerobase.hoops.friends.dto.CancelFriendDto;
import com.zerobase.hoops.friends.dto.DeleteFriendDto;
import com.zerobase.hoops.friends.dto.FriendListDto;
import com.zerobase.hoops.friends.dto.InviteFriendListDto;
import com.zerobase.hoops.friends.dto.RejectFriendDto;
import com.zerobase.hoops.friends.dto.RequestFriendListDto;
import com.zerobase.hoops.friends.dto.SearchFriendListDto;
import com.zerobase.hoops.friends.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/friend")
@RequiredArgsConstructor
@Tag(name = "FRIEND", description = "친구 API")
public class FriendController {

  private final FriendService friendService;

  @Operation(summary = "친구 신청")
  @ApiResponse(responseCode = "200", description = "친구 신청 성공",
      content = @Content(schema = @Schema(implementation = ApplyFriendDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/apply")
  public ResponseEntity<ApplyFriendDto.Response> applyFriend(
      @RequestBody @Validated ApplyFriendDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} applyFriend start", user.getLoginId());
    ApplyFriendDto.Response result = friendService.validApplyFriend
        (request, user);
    log.info("loginId = {} applyFriend end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 신청 취소")
  @ApiResponse(responseCode = "200", description = "친구 신청 취소 성공",
      content = @Content(schema = @Schema(implementation = CancelFriendDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/cancel")
  public ResponseEntity<CancelFriendDto.Response> cancelFriend(
      @RequestBody @Validated CancelFriendDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} cancelFriend start", user.getLoginId());
    CancelFriendDto.Response result = friendService.validCancelFriend
        (request, user);
    log.info("loginId = {} cancelFriend end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 수락")
  @ApiResponse(responseCode = "200", description = "친구 수락 성공",
      content = @Content(schema = @Schema(implementation = AcceptFriendDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/accept")
  public ResponseEntity<AcceptFriendDto.Response> acceptFriend(
      @RequestBody @Validated AcceptFriendDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} acceptFriend start", user.getLoginId());
    AcceptFriendDto.Response result = friendService.validAcceptFriend
        (request, user);
    log.info("loginId = {} acceptFriend end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 거절")
  @ApiResponse(responseCode = "200", description = "친구 거절 성공",
      content = @Content(schema = @Schema(implementation = RejectFriendDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/reject")
  public ResponseEntity<RejectFriendDto.Response> rejectFriend(
      @RequestBody @Validated RejectFriendDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} rejectFriend start", user.getLoginId());
    RejectFriendDto.Response result = friendService.validRejectFriend
        (request, user);
    log.info("loginId = {} rejectFriend end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 삭제")
  @ApiResponse(responseCode = "200", description = "친구 삭제 성공",
      content = @Content(schema = @Schema(implementation = DeleteFriendDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/delete")
  public ResponseEntity<DeleteFriendDto.Response> deleteFriend(
      @RequestBody @Validated DeleteFriendDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} deleteFriend start", user.getLoginId());
    DeleteFriendDto.Response result = friendService.validDeleteFriend
        (request, user);
    log.info("loginId = {} deleteFriend end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 검색")
  @ApiResponse(responseCode = "200", description = "친구 검색 성공",
      content = @Content(schema = @Schema(implementation = SearchFriendListDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/search")
  public ResponseEntity<Page<SearchFriendListDto.Response>> searchFriend(
      @Parameter(name = "nickName", description = "닉네임", example = "홍길동",
          required = true)
      @RequestParam String nickName,
      @Parameter(name = "page", description = "페이지 번호", example = "0",
          required = true)
      @RequestParam(defaultValue = "0") int page,
      @Parameter(name = "size", description = "페이지 크기", example = "10",
          required = true)
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal UserEntity user) {

    log.info("loginId = {} searchFriend start", user.getLoginId());
    Pageable pageable = PageRequest.of(page, size);
    Page<SearchFriendListDto.Response> result =
        friendService.validSearchFriend(nickName, pageable, user);
    log.info("loginId = {} searchFriend end", user.getLoginId());

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "친구 리스트 조회")
  @ApiResponse(responseCode = "200", description = "친구 리스트 조회 성공",
      content = @Content(schema = @Schema(implementation = FriendListDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/myfriends")
  public ResponseEntity<Map<String, List<FriendListDto.Response>>> getMyFriendList(
      @Parameter(name = "page", description = "페이지 번호", example = "0",
          required = true)
      @RequestParam(defaultValue = "0") int page,
      @Parameter(name = "size", description = "페이지 크기", example = "10",
          required = true)
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal UserEntity user) {

    log.info("loginId = {} getMyFriendList start", user.getLoginId());
    Pageable pageable = PageRequest.of(page, size);
    List<FriendListDto.Response> result =
        friendService.validGetMyFriendList(pageable, user);
    log.info("loginId = {} getMyFriendList end", user.getLoginId());

    return ResponseEntity.ok(Map.of("myFriendList", result));
  }

  @Operation(summary = "경기 초대 친구 리스트 조회")
  @ApiResponse(responseCode = "200", description = "경기 초대 친구 리스트 성공",
      content = @Content(schema = @Schema(implementation = InviteFriendListDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/invite/list")
  public ResponseEntity<Page<InviteFriendListDto.Response>> getMyInviteFriendList(
      @RequestParam Long gameId,

      @Parameter(name = "page", description = "페이지 번호", example = "0",
          required = true)
      @RequestParam(defaultValue = "0") int page,
      @Parameter(name = "size", description = "페이지 크기", example = "10",
          required = true)
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal UserEntity user) {

    log.info("loginId = {} getMyInviteFriendList start", user.getLoginId());
    Pageable pageable = PageRequest.of(page, size);
    Page<InviteFriendListDto.Response> result =
        friendService.validGetMyInviteFriendList(gameId, pageable, user);
    log.info("loginId = {} getMyInviteFriendList end", user.getLoginId());

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "내가 친구 요청 받은 리스트 조회")
  @ApiResponse(responseCode = "200", description = "내가 친구 요청 받은 리스트 성공",
      content = @Content(schema = @Schema(implementation = RequestFriendListDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/requestFriendList")
  public ResponseEntity<Map<String, List<RequestFriendListDto.Response>>> getRequestFriendList(
      @Parameter(name = "page", description = "페이지 번호", example = "0",
          required = true)
      @RequestParam(defaultValue = "0") int page,
      @Parameter(name = "size", description = "페이지 크기", example = "10",
          required = true)
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal UserEntity user) {

    log.info("loginId = {} getRequestFriendList start", user.getLoginId());
    Pageable pageable = PageRequest.of(page, size, Direction.ASC,
        "createdDateTime");
    List<RequestFriendListDto.Response> result =
        friendService.validGetRequestFriendList(pageable, user);
    log.info("loginId = {} getRequestFriendList end", user.getLoginId());
    return ResponseEntity.ok(Map.of("requestFriendList", result));
  }

}
