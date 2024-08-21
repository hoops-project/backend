package com.zerobase.hoops.friends.dto;

import com.zerobase.hoops.commonResponse.swaggerSchema.ReportResponse;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

public class SearchFriendResponse {

  @Getter
  @Schema(name = "SearchFriendPage", description = "친구 검색 리스트 페이지")
  public static class SearchFriendPage {

    @Schema(description = "페이지의 항목 리스트")
    private List<SearchFriend> content;

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
  }

  @Getter
  @Schema(name = "Pageable", description = "Pageable")
  public static class Pageable {

    private int pageNumber;
    private int pageSize;
    private ReportResponse.Sort sort;
    private long offset;
    private boolean paged;
    private boolean unpaged;
  }

  @Getter
  @Schema(name = "Sort", description = "정렬 정보")
  public static class Sort {
    private boolean empty;
    private boolean unsorted;
    private boolean sorted;
  }

  @Getter
  @Schema(name = "SearchFriend", description = "친구 검색")
  public static class SearchFriend {

    @Schema(description = "친구 검색 유저 pk", example = "3")
    private Long userId;

    @Schema(description = "친구 검색 유저 생년월일", example = "2004-01-01")
    private LocalDate birthday;

    @Schema(description = "친구 검색 유저 성별", example = "MALE")
    private GenderType gender;

    @Schema(description = "친구 검색 유저 닉네임", example = "키티")
    private String nickName;

    @Schema(description = "친구 검색 유저 플레이스타일", example = "BALANCE")
    private PlayStyleType playStyle;

    @Schema(description = "친구 검색 유저 능력", example = "SPEED")
    private AbilityType ability;

    @Schema(description = "친구 검색 유저 매너점수", example = "2.5")
    private String mannerPoint;

    @Schema(description = "친구 pk", example = "2")
    private Long friendId;
  }

}
