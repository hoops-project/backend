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
  private Long reportId;
  private Long userId;
  private String userName;
  private String mannerPoint;
  private GenderType gender;
  private AbilityType ability;
  private PlayStyleType playStyle;

  public static ReportListResponseDto of (ReportEntity reportEntity) {

    return ReportListResponseDto.builder()
        .reportId(reportEntity.getId())
        .userId(reportEntity.getReportedUser().getUserId())
        .userName(reportEntity.getReportedUser().getName())
        .mannerPoint(reportEntity.getReportedUser().getStringAverageRating())
        .gender(reportEntity.getReportedUser().getGender())
        .ability(reportEntity.getReportedUser().getAbility())
        .playStyle(reportEntity.getReportedUser().getPlayStyle())
        .build();
  }
}
