package com.zerobase.hoops.reports.controller;

import com.zerobase.hoops.commonResponse.ApiResponse;
import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.dto.ReportListResponseDto;
import com.zerobase.hoops.reports.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
  public ResponseEntity<Page<ReportListResponseDto>> reportList(
      @RequestParam(value = "page",defaultValue = "0") @Positive int page,
      @RequestParam(value = "size",defaultValue = "10") @Positive int size) {
    return ResponseEntity.ok()
        .body(this.reportService.reportList(page, size));
  }

  @PreAuthorize("hasRole('OWNER')")
  @GetMapping("/contents/{report_id}")
  public ResponseEntity<ApiResponse> reportContents(
      @PathVariable("report_id") @NotBlank String report_id) {
    return ResponseEntity.ok().body(
        new ApiResponse("신고내역",
            this.reportService.reportContents(report_id)));
  }


}
