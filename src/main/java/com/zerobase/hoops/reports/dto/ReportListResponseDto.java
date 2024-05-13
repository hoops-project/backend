package com.zerobase.hoops.reports.dto;

import com.zerobase.hoops.entity.ReportEntity;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportListResponseDto {
  private Long userId;
  private String userName;
  private String mannerPoint;
  private GenderType gender;
  private AbilityType abilityType;
  private PlayStyleType playStyleType;

  public static ReportListResponseDto of (ReportEntity reportEntity) {

    return ReportListResponseDto.builder()
        .userId(reportEntity.getReportedUser().getUserId())
        .userName(reportEntity.getReportedUser().getName())
        .mannerPoint(reportEntity.getReportedUser().getStringAverageRating())
        .gender(reportEntity.getReportedUser().getGender())
        .abilityType(reportEntity.getReportedUser().getAbility())
        .playStyleType(reportEntity.getReportedUser().getPlayStyle())
        .build();
  }
}
