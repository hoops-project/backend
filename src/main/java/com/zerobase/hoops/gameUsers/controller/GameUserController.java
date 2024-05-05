
package com.zerobase.hoops.gameUsers.controller;

import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameUsers.dto.GameSearchResponse;
import com.zerobase.hoops.gameUsers.service.GameUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ResponseEntity<List<GameSearchResponse>> searchMain() {
    return ResponseEntity.ok(gameUserService.searchMain());
  }

  @GetMapping("/search-city")
  public ResponseEntity<List<GameSearchResponse>> searchCity(
      @RequestParam CityName cityName) {
    return ResponseEntity.ok(gameUserService.searchCity(
        cityName));
  }

  @GetMapping("/search-address")
  public ResponseEntity<List<GameSearchResponse>> searchAddress(
      @RequestParam String address) {
    return ResponseEntity.ok(gameUserService.searchAddress(address));
  }
}