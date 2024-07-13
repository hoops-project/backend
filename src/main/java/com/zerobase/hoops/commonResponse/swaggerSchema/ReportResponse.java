package com.zerobase.hoops.commonResponse.swaggerSchema;

import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ReportResponse {

  // Report

  @Getter
  @AllArgsConstructor
  @Schema(name = "ReportUser", description = "유저 신고 응답")
  public static class ReportUser implements SwaggerApiResponse {

    @Schema(description = "응답 메시지", example = "유저신고")
    private String title;
    @Schema(description = "응답 상태", example = "Success")
    private String detail;

  }

  @Getter
  @Schema(name = "PageReportUsersList", description = "신고된 유저 리스트 페이지")
  public static class PageReportUsersList {

    @Schema(description = "페이지의 항목 리스트")
    private List<ReportUsersList> content;

    @Schema(description = "Pageable")
    private Pageable pageable;

    @Schema(description = "마지막 페이지 여부")
    private boolean last;

    @Schema(description = "총 요소 수")
    private long totalElements;

    @Schema(description = "총 페이지 수")
    private int totalPages;

    @Schema(description = "페이지 크기")
    private int size;

    @Schema(description = "현재 페이지 번호")
    private int number;

    @Schema(description = "정렬 정보")
    private Sort sort;

    @Schema(description = "첫 페이지 여부")
    private boolean first;

    @Schema(description = "현재 페이지의 요소 수")
    private int numberOfElements;

    @Schema(description = "페이지가 비어있는지 여부")
    private boolean empty;

    public PageReportUsersList(List<ReportUsersList> content,
        Pageable pageable, boolean last, long totalElements,
        int totalPages, int size, int number, Sort sort, boolean first,
        int numberOfElements, boolean empty) {
      this.content = content;
      this.pageable = pageable;
      this.last = last;
      this.totalElements = totalElements;
      this.totalPages = totalPages;
      this.size = size;
      this.number = number;
      this.sort = sort;
      this.first = first;
      this.numberOfElements = numberOfElements;
      this.empty = empty;
    }
  }

  @Getter
  @Schema(name = "Pageable", description = "Pageable")
  public static class Pageable {

    private int pageNumber;
    private int pageSize;
    private Sort sort;
    private long offset;
    private boolean unpaged;
    private boolean paged;
  }

  @Getter
  @Schema(name = "Sort", description = "정렬 정보")
  public static class Sort {

    private boolean empty;
    private boolean sorted;
    private boolean unsorted;
  }

  @Getter
  @Schema(name = "ReportUsersList", description = "신고 내역")
  public static class ReportUsersList {

    @Schema(description = "신고 PK", example = "2")
    private Long reportId;

    @Schema(description = "유저 PK", example = "5")
    private Long userId;

    @Schema(description = "유저 이름", example = "김갑수")
    private String userName;

    @Schema(description = "매너 포인트", example = "3.5")
    private String mannerPoint;

    @Schema(description = "성별", example = "MALE")
    private GenderType gender;

    @Schema(description = "능력", example = "SHOOT")
    private AbilityType ability;

    @Schema(description = "플레이 스타일", example = "AGGRESSIVE")
    private PlayStyleType playStyle;
  }

  @Getter
  @AllArgsConstructor
  @Schema(name = "ReportContent", description = "신고 내역")
  public static class ReportContent implements SwaggerApiResponse {

    @Schema(description = "응답 메시지", example = "신고내역")
    private String title;
    @Schema(description = "응답 상태", example = "정말 비매너로 경기를 하고 욕설을 너무 많이해서 불화가 많습니다.")
    private String detail;

  }
}
