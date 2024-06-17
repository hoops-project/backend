package com.zerobase.hoops.gameCreator.controller;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.dto.GameDto;
import com.zerobase.hoops.gameCreator.dto.GameDto.DetailResponse;
import com.zerobase.hoops.gameCreator.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/game-creator")
@RequiredArgsConstructor
@Tag(name = "3. GAME")
public class GameController {

  private final GameService gameService;

  /**
   * 경기 생성
   */
  @Operation(summary = "경기 생성")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/game/create")
  public ResponseEntity<Map<String, String>> createGame(
      @RequestBody @Validated GameDto.CreateRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} createGame start", user.getLoginId());
    String message = gameService.validCreateGame(request, user);
    log.info("loginId = {} createGame end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  /**
   * 경기 상세 조회
   */
  @Operation(summary = "경기 상세 조회")
  @GetMapping("/game/detail")
  public ResponseEntity<DetailResponse> getGameDetail(
      @RequestParam("gameId") Long gameId) {
    log.info("getGameDetail start");
    GameDto.DetailResponse result = gameService.validGetGameDetail(gameId);
    log.info("getGameDetail start");
    return ResponseEntity.ok(result);
  }

  /**
   * 경기 수정
   */
  @Operation(summary = "경기 수정")
  @PreAuthorize("hasRole('USER')")
  @PutMapping("/game/update")
  public ResponseEntity<Map<String, String>> updateGame(
      @RequestBody @Validated GameDto.UpdateRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} updateGame start", user.getLoginId());
    String message = gameService.validUpdateGame(request, user);
    log.info("loginId = {} updateGame end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

  /**
   * 경기 삭제
   */
  @Operation(summary = "경기 삭제 및 팀원 탈퇴")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/game/delete")
  public ResponseEntity<Map<String, String>> deleteGameOrWithdrewGame(
      @RequestBody @Validated GameDto.DeleteRequest request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} deleteGameOrWithdrewGame start", user.getLoginId());
    String message = gameService.validDeleteGame(request, user);
    log.info("loginId = {} deleteGameOrWithdrewGame end", user.getLoginId());
    return ResponseEntity.ok(Map.of("message", message));
  }

}
