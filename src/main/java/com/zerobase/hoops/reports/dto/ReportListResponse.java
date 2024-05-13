package com.zerobase.hoops.reports.dto;

import com.zerobase.hoops.entity.ReportEntity;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class ReportListResponse {
  private Long userId;
  private String userName;
  private String mannerPoint;
  private GenderType gender;
  private AbilityType abilityType;
  private PlayStyleType playStyleType;

  public static ReportListResponse of (ReportEntity reportEntity) {

    return ReportListResponse.builder()
        .userId(reportEntity.getReportedUser().getUserId())
        .userName(reportEntity.getReportedUser().getName())
        .mannerPoint(reportEntity.getReportedUser().getStringAverageRating())
        .gender(reportEntity.getReportedUser().getGender())
        .abilityType(reportEntity.getReportedUser().getAbility())
        .playStyleType(reportEntity.getReportedUser().getPlayStyle())
        .build();
  }
}
