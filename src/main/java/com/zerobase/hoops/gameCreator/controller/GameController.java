package com.zerobase.hoops.gameCreator.controller;

import com.zerobase.hoops.gameCreator.dto.GameDto;
import com.zerobase.hoops.gameCreator.dto.GameDto.DetailResponse;
import com.zerobase.hoops.gameCreator.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
      @RequestBody @Validated GameDto.CreateRequest request) {
    String message = gameService.validCreateGame(request);
    return ResponseEntity.ok(Map.of("message", message));
  }

  /**
   * 경기 상세 조회
   */
  @Operation(summary = "경기 상세 조회")
  @GetMapping("/game/detail")
  public ResponseEntity<DetailResponse> getGameDetail(@RequestParam("gameId") Long gameId) {
    GameDto.DetailResponse result = gameService.getGameDetail(gameId);
    return ResponseEntity.ok(result);
  }

  /**
   * 경기 수정
   */
  @Operation(summary = "경기 수정")
  @PreAuthorize("hasRole('USER')")
  @PutMapping("/game/update")
  public ResponseEntity<Map<String, String>> updateGame(
      @RequestBody @Validated GameDto.UpdateRequest request) {
    String message = gameService.validUpdateGame(request);
    return ResponseEntity.ok(Map.of("message", message));
  }

  /**
   * 경기 삭제
   */
  @Operation(summary = "경기 삭제 및 팀원 탈퇴")
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/game/delete")
  public ResponseEntity<Map<String, String>> deleteGameOrWithdrewGame(
      @RequestBody @Validated GameDto.DeleteRequest request) {
    String message = gameService.validDeleteGame(request);
    return ResponseEntity.ok(Map.of("message", message));
  }

}
