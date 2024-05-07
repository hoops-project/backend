package com.zerobase.hoops.reports.service;

import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.reports.dto.ReportDto;
import com.zerobase.hoops.reports.repository.ReportRepository;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ReportService {

  public final ReportRepository reportRepository;
  public final UserRepository userRepository;
  private final JwtTokenExtract jwtTokenExtract;

  public void reportUser(ReportDto request) {
    String user = jwtTokenExtract.currentUser().getEmail();
    checkValidUser(request);

    reportRepository.save(request.toEntity(user));
  }

  private void checkValidUser(ReportDto request) {
    userRepository.findByEmail(
            jwtTokenExtract.currentUser().getEmail())
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    userRepository.findByEmail(
            request.getReportedUserEmail())
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));
  }
}
