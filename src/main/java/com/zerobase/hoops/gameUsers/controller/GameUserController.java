
package com.zerobase.hoops.gameUsers.controller;

import com.zerobase.hoops.commonResponse.ApiResponseFactory;
import com.zerobase.hoops.commonResponse.BasicApiResponse;
import com.zerobase.hoops.commonResponse.swaggerSchema.GameUserResponse;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.dto.MannerPointDto;
import com.zerobase.hoops.gameUsers.dto.MannerPointListResponse;
import com.zerobase.hoops.gameUsers.dto.UserJoinsGameDto;
import com.zerobase.hoops.gameUsers.service.GameUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/game-user")
@RequiredArgsConstructor
@Tag(name = "GAME-USER")
public class GameUserController {

  private final GameUserService gameUserService;
  private final ApiResponseFactory apiResponseFactory;

  @Operation(summary = "경기 검색 (동적쿼리)")
  @ApiResponse(responseCode = "200", description = "경기조건을 필터링하여 검색")
  @GetMapping("/search")
  public ResponseEntity<Page<GameSearchResponse>> findFilteredGames(
      @Parameter(name = "localDate", description = "검색할 날짜 입력")
      @RequestParam LocalDate localDate,
      @RequestParam(required = false) CityName cityName,
      @RequestParam(required = false) FieldStatus fieldStatus,
      @RequestParam(required = false) Gender gender,
      @RequestParam(required = false) MatchFormat matchFormat,
      @RequestParam(value = "page", defaultValue = "0") @Positive int page,
      @RequestParam(value = "size", defaultValue = "5") @Positive int size) {
    return ResponseEntity.ok(
        gameUserService.findFilteredGames(localDate,
            cityName, fieldStatus, gender, matchFormat, page, size));
  }

  @Operation(summary = "경기 검색 (주소)")
  @ApiResponse(responseCode = "200", description = "경기장 주소 일부만 입력해도 검색가능")
  @GetMapping("/search-address")
  public ResponseEntity<Page<GameSearchResponse>> searchAddress(
      @Parameter(name = "address ", description = "경기장 주소", required = true)
      @RequestParam(value = "page", defaultValue = "0") @Positive int page,
      @RequestParam(value = "size", defaultValue = "5") @Positive int size,
      @RequestParam String address) {
    return ResponseEntity.ok(gameUserService.searchAddress(address, page, size));
  }

  @Operation(summary = "경기 참가 여부")
  @ApiResponse(responseCode = "200", description = "유저가 경기 신청을 할 경우")
  @Parameter(name = "gameId", description = "게임 엔티티 pk", required = true)
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/game-in-out")
  public UserJoinsGameDto.Response participateInGame(
      @RequestBody @Valid UserJoinsGameDto.Request request) {
    return UserJoinsGameDto.Response.from(
        gameUserService.participateInGame(request.getGameId()));
  }

  @Operation(summary = "나의 현재 참여하고 있는 경기 리스트")
  @ApiResponse(responseCode = "200", description = "유저가 현재 참여하고 있는 경기 목록 리스트")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/my-current-game-list")
  public ResponseEntity<Page<GameSearchResponse>> myCurrentGameList(
      @RequestParam(value = "page", defaultValue = "0") @Positive int page,
      @RequestParam(value = "size", defaultValue = "5") @Positive int size) {
    return ResponseEntity.ok(
        gameUserService.myCurrentGameList(page, size));
  }

  @Operation(summary = "나의 지난 경기 리스트")
  @ApiResponse(responseCode = "200", description = "유저가 참여했던 지난 경기 목록 리스트")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/my-last-game-list")
  public ResponseEntity<Page<GameSearchResponse>> myLastGameList(
      @RequestParam(value = "page", defaultValue = "0") @Positive int page,
      @RequestParam(value = "size", defaultValue = "5") @Positive int size) {
    return ResponseEntity.ok(gameUserService.myLastGameList(page, size));
  }

  @Operation(summary = "매너 점수 평가 리스트")
  @ApiResponse(responseCode = "200", description = "해당 경기에 참여했던 유저들의 목록을 List 형식으로 출력")
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/manner-point/{gameId}")
  public ResponseEntity<List<MannerPointListResponse>> getMannerPoint(
      @Parameter(description = "게임 엔티티 pk", name = "gameId")
      @PathVariable("gameId") @NotBlank String gameId) {
    return ResponseEntity.ok(gameUserService.getMannerPoint(gameId));
  }

  @Operation(summary = "매너 점수 평가")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "매너점수 평가",
          content = @Content(schema = @Schema(implementation = GameUserResponse.GiveMannerPoint.class))),
  })
  @Parameters({
      @Parameter(name = "receiverId", description = "상대방 유저 pk", required = true),
      @Parameter(name = "gameId", description = "게임 엔티티 pk", required = true),
      @Parameter(name = "point", description = "매너포인트 (1~5)", required = true)
  })
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/manner-point")
  public ResponseEntity<BasicApiResponse> saveMannerPoint(
      @RequestBody @Valid MannerPointDto request) {
    gameUserService.saveMannerPoint(request);
    return ResponseEntity.ok()
        .body(apiResponseFactory.createSuccessResponse("매너점수평가"));
  }
}