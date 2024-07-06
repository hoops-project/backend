package com.zerobase.hoops.gameCreator.controller;

import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.gameCreator.dto.CreateGameDto;
import com.zerobase.hoops.gameCreator.dto.DeleteGameDto;
import com.zerobase.hoops.gameCreator.dto.DetailGameDto;
import com.zerobase.hoops.gameCreator.dto.UpdateGameDto;
import com.zerobase.hoops.gameCreator.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "GAME-CREATOR", description = "경기 개설자 API")
public class GameController {

  private final GameService gameService;

  @Operation(summary = "경기 생성")
  @ApiResponse(responseCode = "200", description = "경기 생성 성공",
      content = @Content(schema = @Schema(implementation =
          CreateGameDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/game/create")
  public ResponseEntity<CreateGameDto.Response> createGame(
      @RequestBody @Validated CreateGameDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} createGame start", user.getLoginId());
    CreateGameDto.Response result = gameService.validCreateGame(request, user);
    log.info("loginId = {} createGame end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 상세 조회", security = {})
  @ApiResponse(responseCode = "200", description = "경기 상세 조회 성공", content =
  @Content(schema = @Schema(implementation = DetailGameDto.Response.class)))

  @GetMapping("/game/detail")
  public ResponseEntity<DetailGameDto.Response> getGameDetail(
      @RequestParam("gameId")
      @Parameter(name = "gameId", description = "경기 아이디",
          example = "1", required = true) Long gameId) {
    log.info("getGameDetail start");
    DetailGameDto.Response result = gameService.validGetGameDetail(gameId);
    log.info("getGameDetail start");
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 수정")
  @ApiResponse(responseCode = "200", description = "경기 수정 성공", content =
  @Content(schema = @Schema(implementation = UpdateGameDto.Response.class)))
  @PreAuthorize("hasRole('USER')")
  @PutMapping("/game/update")
  public ResponseEntity<UpdateGameDto.Response> updateGame(
      @RequestBody @Validated UpdateGameDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} updateGame start", user.getLoginId());
    UpdateGameDto.Response result = gameService.validUpdateGame(request, user);
    log.info("loginId = {} updateGame end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "경기 삭제 및 참가자 탈퇴")
  @ApiResponse(responseCode = "200", description = "경기 삭제 및 팀원탈퇴 성공",
      content = @Content(schema = @Schema(implementation = DeleteGameDto.Response.class),
      examples = {
          @ExampleObject(name = "개설자가 경기 삭제시", description = "메세지",
              value = "{\"message\":\"이촌한강공원 농구장에서 3:3할사람 모여라 경기가 삭제되었습니다.\"}"),
          @ExampleObject(name = "참가자가 탈퇴시", description = "메세지",
              value = "{\"message\":\"이촌한강공원 농구장에서 3:3할사람 모여라 경기에 탈퇴했습니다.\"}")
      }))
  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/game/delete")
  public ResponseEntity<DeleteGameDto.Response> deleteGameOrWithdrewGame(
      @RequestBody @Validated DeleteGameDto.Request request,
      @AuthenticationPrincipal UserEntity user) {
    log.info("loginId = {} deleteGameOrWithdrewGame start", user.getLoginId());
    DeleteGameDto.Response result = gameService.validDeleteGame(request, user);
    log.info("loginId = {} deleteGameOrWithdrewGame end", user.getLoginId());
    return ResponseEntity.ok(result);
  }

}
