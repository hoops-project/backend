package com.zerobase.hoops.reports.dto;

import com.zerobase.hoops.document.ReportDocument;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportListResponseDto {
  private String reportId;
  private String userId;
  private String userName;
  private String mannerPoint;
  private GenderType gender;
  private AbilityType ability;
  private PlayStyleType playStyle;

  public static ReportListResponseDto of (ReportDocument reportDocument) {

    return ReportListResponseDto.builder()
        .reportId(reportDocument.getId())
        .userId(reportDocument.getReportedUser().getId())
        .userName(reportDocument.getReportedUser().getName())
        .mannerPoint(reportDocument.getReportedUser().getStringAverageRating())
        .gender(reportDocument.getReportedUser().getGender())
        .ability(reportDocument.getReportedUser().getAbility())
        .playStyle(reportDocument.getReportedUser().getPlayStyle())
        .build();
  }
}
