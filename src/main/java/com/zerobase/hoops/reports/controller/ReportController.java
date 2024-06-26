package com.zerobase.hoops.reports.controller;

import com.zerobase.hoops.commonResponse.ApiResponseFactory;
import com.zerobase.hoops.commonResponse.BasicApiResponse;
import com.zerobase.hoops.commonResponse.swaggerSchema.ErrorResponse;
import com.zerobase.hoops.commonResponse.swaggerSchema.ReportResponse;
import com.zerobase.hoops.commonResponse.swaggerSchema.ReportResponse.ReportContent;
import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.dto.ReportListResponseDto;
import com.zerobase.hoops.reports.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
@Tag(name = "REPORT")
public class ReportController {

  private final ReportService reportService;
  private final ApiResponseFactory apiResponseFactory;

  @Operation(summary = "유저간 신고 기능")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "유저 신고 기능 성공",
          content = @Content(schema = @Schema(implementation = ReportResponse.ReportUser.class))),
      @ApiResponse(responseCode = "400", description = "커스텀 에러",
          content = @Content(schema = @Schema(implementation = ErrorResponse.CustomError.class))),
      @ApiResponse(responseCode = "403", description = "리프레시 토큰 만료",
          content = @Content(schema = @Schema(implementation = ErrorResponse.ExpiredRefreshToken.class))),
      @ApiResponse(responseCode = "500", description = "서버 에러 표시",
          content = @Content(schema = @Schema(implementation = ErrorResponse.ServerError.class)))
  }
  )
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/user")
  public ResponseEntity<BasicApiResponse> report(
      @RequestBody @Valid ReportDto request) {
    this.reportService.reportUser(request);
    return ResponseEntity.ok().body(
        apiResponseFactory.createSuccessResponse("유저신고"));
  }

  @Operation(summary = "신고된 유저 리스트")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "신고된 리스트 출력",
          content = @Content(schema = @Schema(implementation = ReportResponse.PageReportUsersList.class))),
      @ApiResponse(responseCode = "400", description = "커스텀 에러",
          content = @Content(schema = @Schema(implementation = ErrorResponse.CustomError.class))),
      @ApiResponse(responseCode = "403", description = "리프레시 토큰 만료",
          content = @Content(schema = @Schema(implementation = ErrorResponse.ExpiredRefreshToken.class))),
      @ApiResponse(responseCode = "500", description = "서버 에러 표시",
          content = @Content(schema = @Schema(implementation = ErrorResponse.ServerError.class)))
  })
  @PreAuthorize("hasRole('OWNER')")
  @GetMapping("/user-list")
  public ResponseEntity<Page<ReportListResponseDto>> reportList(
      @RequestParam(value = "page", defaultValue = "0") @Positive int page,
      @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
    return ResponseEntity.ok()
        .body(this.reportService.reportList(page, size));
  }

  @Operation(summary = "신고 내역 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "신고 내역 조회",
          content = @Content(schema = @Schema(implementation = ReportContent.class))),
      @ApiResponse(responseCode = "400", description = "커스텀 에러",
          content = @Content(schema = @Schema(implementation = ErrorResponse.CustomError.class))),
      @ApiResponse(responseCode = "403", description = "리프레시 토큰 만료",
          content = @Content(schema = @Schema(implementation = ErrorResponse.ExpiredRefreshToken.class))),
      @ApiResponse(responseCode = "500", description = "서버 에러 표시",
          content = @Content(schema = @Schema(implementation = ErrorResponse.ServerError.class)))
  })
  @PreAuthorize("hasRole('OWNER')")
  @GetMapping("/contents/{report_id}")
  public ResponseEntity<BasicApiResponse> reportContents(
      @Parameter(description = "신고 유저 PK", name = "report_id")
      @PathVariable("report_id") @NotBlank String report_id) {
    return ResponseEntity.ok().body(
        apiResponseFactory.createSuccessWithDetailResponse(
            "신고 내역", this.reportService.reportContents(report_id)));
  }


}
