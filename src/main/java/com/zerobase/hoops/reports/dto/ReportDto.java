package com.zerobase.hoops.reports.dto;

import com.zerobase.hoops.entity.ReportEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDto {


  @NotBlank
  @Email(message = "본인의 올바른 이메일 주소를 입력해주세요")
  private String userEmail;

  @NotBlank
  @Email(message = "신고하려는 유저의 올바른 이메일 주소를 입력해주세요")
  private String reportedUserEmail;

  @NotBlank
  @Size(min = 30, max = 255, message = "최소 30자 이상 255자 이하로 신고 내용을 작성해주세요")
  private String content;

  public ReportEntity toEntity() {
    return ReportEntity.builder()
        .userId(this.userEmail)
        .reportedId(this.reportedUserEmail)
        .content(this.content)
        .build();
  }

}
