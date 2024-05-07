
package com.zerobase.hoops.gameUsers.controller;

import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.dto.UserJoinsGameDto;
import com.zerobase.hoops.gameUsers.service.GameUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ResponseEntity<List<GameSearchResponse>> findFilteredGames(
      @RequestParam(required = false) LocalDate localDate,
      @RequestParam(required = false) CityName cityName,
      @RequestParam(required = false) FieldStatus fieldStatus,
      @RequestParam(required = false) Gender gender,
      @RequestParam(required = false) MatchFormat matchFormat) {
    return ResponseEntity.ok(
        gameUserService.findFilteredGames(localDate,
            cityName, fieldStatus, gender, matchFormat));
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
  public ResponseEntity<List<GameSearchResponse>> myCurrentGameList() {
    return ResponseEntity.ok(gameUserService.myCurrentGameList());
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/my-last-game-list")
  public ResponseEntity<List<GameSearchResponse>> myLastGameList() {
    return ResponseEntity.ok(gameUserService.myLastGameList());
  }

}