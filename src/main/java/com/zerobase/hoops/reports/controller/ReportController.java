package com.zerobase.hoops.reports.controller;

import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  // @PreAuthorize("hasRole('USER')")
  // 로그인 기능 완성되면 해당 부분 확인해볼 것
  @PostMapping("/user")
  public ResponseEntity<?> report(
      @RequestBody @Valid ReportDto request) {
    this.reportService.reportUser(request);
    return ResponseEntity.ok().body(
        new ApiResponse("유저신고", "Success"));
  }
}
