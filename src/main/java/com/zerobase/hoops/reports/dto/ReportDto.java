package com.zerobase.hoops.reports.dto;

import static com.zerobase.hoops.util.Common.getNowDateTime;

import com.zerobase.hoops.document.ReportDocument;
import com.zerobase.hoops.document.UserDocument;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDto {

  @NotBlank
  private String reportedUserId;

  @NotBlank
  @Size(min = 30, max = 255, message = "최소 30자 이상 255자 이하로 신고 내용을 작성해주세요")
  private String content;

  public ReportDocument toEntity(UserDocument user, UserDocument reportedUser
      , long reportId) {
    return ReportDocument.builder()
        .id(Long.toString(reportId))
        .user(user)
        .reportedUser(reportedUser)
        .createdDateTime(getNowDateTime())
        .content(this.content)
        .build();
  }

}
