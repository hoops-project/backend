package com.zerobase.hoops.reports.dto;

import com.zerobase.hoops.entity.ReportEntity;
import com.zerobase.hoops.entity.UserEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDto {


  @NotNull
  @Min(1)
  private Long reportedUserId;

  @NotBlank
  @Size(min = 30, max = 255, message = "최소 30자 이상 255자 이하로 신고 내용을 작성해주세요")
  private String content;

  public ReportEntity toEntity(UserEntity user, UserEntity reportedUser) {
    return ReportEntity.builder()
        .user(user)
        .reportedUser(reportedUser)
        .content(this.content)
        .build();
  }

}
