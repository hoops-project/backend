
package com.zerobase.hoops.gameUsers.controller;

import com.zerobase.hoops.commonResponse.ApiResponse;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.dto.MannerPointDto;
import com.zerobase.hoops.gameUsers.dto.MannerPointListResponse;
import com.zerobase.hoops.gameUsers.dto.UserJoinsGameDto;
import com.zerobase.hoops.gameUsers.service.GameUserService;
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
@Tag(name = "4. GAME-USER")
public class GameUserController {

  private final GameUserService gameUserService;

  @GetMapping("/search")
  public ResponseEntity<Page<GameSearchResponse>> findFilteredGames(
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

  @GetMapping("/search-address")
  public ResponseEntity<List<GameSearchResponse>> searchAddress(
      @RequestParam String address) {
    return ResponseEntity.ok(gameUserService.searchAddress(address));
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/game-in-out")
  public UserJoinsGameDto.Response participateInGame(
      @RequestBody @Valid UserJoinsGameDto.Request request) {
    return UserJoinsGameDto.Response.from(
        gameUserService.participateInGame(request.getGameId()));
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/my-current-game-list")
  public ResponseEntity<Page<GameSearchResponse>> myCurrentGameList(
      @RequestParam(value = "page", defaultValue = "0") @Positive int page,
      @RequestParam(value = "size", defaultValue = "5") @Positive int size) {
    return ResponseEntity.ok(
        gameUserService.myCurrentGameList(page, size));
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/my-last-game-list")
  public ResponseEntity<Page<GameSearchResponse>> myLastGameList(
      @RequestParam(value = "page", defaultValue = "0") @Positive int page,
      @RequestParam(value = "size", defaultValue = "5") @Positive int size) {
    return ResponseEntity.ok(gameUserService.myLastGameList(page, size));
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/manner-point/{gameId}")
  public ResponseEntity<List<MannerPointListResponse>> getMannerPoint(
      @PathVariable("gameId") @NotBlank String gameId) {
    return ResponseEntity.ok(gameUserService.getMannerPoint(gameId));
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/manner-point")
  public ResponseEntity<ApiResponse> saveMannerPoint(
      @RequestBody @Valid MannerPointDto request) {
    gameUserService.saveMannerPoint(request);
    return ResponseEntity.ok().body(new ApiResponse("매너점수평가", "Success"));
  }
}