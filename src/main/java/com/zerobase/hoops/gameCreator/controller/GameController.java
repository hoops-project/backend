package com.zerobase.hoops.gameCreator.controller;

import com.zerobase.hoops.gameCreator.dto.GameDto;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateResponse;
import com.zerobase.hoops.gameCreator.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
  @PostMapping("/game/create")
  public ResponseEntity<CreateResponse> createGame(
      @RequestBody @Validated GameDto.CreateRequest request) {
    GameDto.CreateResponse result = gameService.createGame(request);
    return ResponseEntity.ok(result);
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
  @PutMapping("/game/update")
  public ResponseEntity<UpdateResponse> updateGame(
      @RequestBody @Validated GameDto.UpdateRequest request) {
    GameDto.UpdateResponse result = gameService.updateGame(request);
    return ResponseEntity.ok(result);
  }

  /**
   * 경기 삭제
   */
  @Operation(summary = "경기 삭제")
  @PatchMapping("/game/delete")
  public ResponseEntity<DeleteResponse> deleteGame(
      @RequestBody @Validated GameDto.DeleteRequest request) {
    GameDto.DeleteResponse result = gameService.deleteGame(request);
    return ResponseEntity.ok(result);
  }

}
