package com.zerobase.hoops.reports.controller;

import com.zerobase.hoops.commonResponse.ApiResponse;
import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.dto.ReportListResponse;
import com.zerobase.hoops.reports.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Tag(name = "4. REPORT")
public class ReportController {

  private final ReportService reportService;

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/user")
  public ResponseEntity<ApiResponse> report(
      @RequestBody @Valid ReportDto request) {
    this.reportService.reportUser(request);
    return ResponseEntity.ok().body(
        new ApiResponse("유저신고", "Success"));
  }

  @PreAuthorize("hasRole('OWNER')")
  @GetMapping("/user-list")
  public ResponseEntity<List<ReportListResponse>> reportList(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok()
        .body(this.reportService.reportList(page, size));
  }

}
