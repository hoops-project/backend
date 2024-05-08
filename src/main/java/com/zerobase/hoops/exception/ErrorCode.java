package com.zerobase.hoops.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  INVALID_INPUT(HttpStatus.BAD_REQUEST.value(), "필수 입력 값이 누락되었습니다."),
  INVALID_PATTERN(HttpStatus.BAD_REQUEST.value(), "형식에 맞게 입력 해야합니다."),
  PAST_BIRTHDAY(HttpStatus.BAD_REQUEST.value(), "생년월일은 과거의 날짜만 입력 가능합니다."),

  MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "메일 발송에 실패하였습니다."),
  WRONG_EMAIL(HttpStatus.NO_CONTENT.value(), "잘못된 이메일 주소입니다."),
  INVALID_NUMBER(HttpStatus.FORBIDDEN.value(), "유효하지 않은 인증번호입니다."),
  USER_NOT_CONFIRM(HttpStatus.BAD_REQUEST.value(), "인증되지 않은 회원입니다."),

  NOT_FOUND_TOKEN(HttpStatus.BAD_REQUEST.value(), "토큰 형식의 값을 찾을 수 없습니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "기간이 만료된 토큰입니다."),
  NOT_MATCHED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "토큰 정보가 일치하지 않습니다."),
  UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST.value(), "지원하지 않는 토큰입니다."),

  DUPLICATED_ID(HttpStatus.CONFLICT.value(), "이미 사용 중인 아이디입니다."),
  DUPLICATED_NICKNAME(HttpStatus.CONFLICT.value(), "이미 사용 중인 별명입니다."),
  DUPLICATED_EMAIL(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "아이디가 존재하지 않습니다."),
  NOT_MATCHED_PASSWORD(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다."),

  // 블랙리스트
  BAN_FOR_10DAYS(HttpStatus.BAD_REQUEST.value(), "10일 후에 다시 이용 가능합니다."),

  // 게임 개설자
  GAME_NOT_FOUND(HttpStatus.NOT_FOUND.value(),  "경기를 찾을 수 없습니다."),
  NOT_GAME_CREATOR(HttpStatus.NOT_ACCEPTABLE.value(), "경기 개최자가 아닙니다."),
  NOT_UPDATE_HEADCOUNT(HttpStatus.BAD_REQUEST.value(),  "설정 하려는 인원수가 수락된 인원수 보다 적습니다."),
  NOT_UPDATE_MAN(HttpStatus.BAD_REQUEST.value(), "여자가 그룹에 참가중입니다."),
  NOT_UPDATE_WOMAN(HttpStatus.BAD_REQUEST.value(), "남자가 그룹에 참가중입니다."),
  NOT_AFTER_THIRTY_MINUTE(HttpStatus.BAD_REQUEST.value(), "경기 시작 시간이 현재 시간의 30분 이후 이면 "
      + "경기 생성 가능 합니다."),
  ALREADY_GAME_CREATED(HttpStatus.BAD_REQUEST.value(), "설정한 경기 시작 시간 30분전 ~ 30분후 사이에"
      + "이미 열린 경기가 있습니다."),
  NOT_UPDATE_STARTDATE(HttpStatus.BAD_REQUEST.value(), "변경 하려는 시작 시간이 기존에"
      + " 설정했던 시작 시간 보다 이후여야 합니다."),
  NOT_DELETE_STARTDATE(HttpStatus.BAD_REQUEST.value(),  "경기 시작 시간 30분 전에 "
      + "경기 삭제 가능 합니다."),
  NOT_PARTICIPANT_FOUND(HttpStatus.NOT_FOUND.value(), "경기 참가 내역을 불러올수 없습니다."),
  ALREADY_GAME_START(HttpStatus.BAD_REQUEST.value(), "경기가 이미 시작되었습니다."),
  NOT_UPDATE_CREATOR(HttpStatus.BAD_REQUEST.value(), "경기 개설자는 수락 상태로 있어야 합니다."),
  FULL_PARTICIPANT(HttpStatus.BAD_REQUEST.value(), "경기에 참가자가 다 찼습니다."),
  NOT_CREATE_THREEONTHREEE(HttpStatus.BAD_REQUEST.value(),  "3:3 경기는 인원이 6 ~ "
      + "9명 으로 설정해야 합니다."),
  NOT_CREATE_FIVEONFIVE(HttpStatus.BAD_REQUEST.value(),  "5:5 경기는 인원이 10 ~ "
      + "15명 으로 설정해야 합니다."),

  // 게임 참가자
  FULL_PEOPLE_GAME(HttpStatus.BAD_REQUEST.value(),"신청 가능한 인원이 초과되어 더 이상 신청할 수 없습니다."),
  OVER_TIME_GAME(HttpStatus.BAD_REQUEST.value(), "신청 가능한 시간이 이미 지났습니다. 더 이상 신청할 수 없습니다."),
  ONLY_FEMALE_GAME(HttpStatus.BAD_REQUEST.value(), "여성만 신청 가능한 경기 입니다."),
  ONLY_MALE_GAME(HttpStatus.BAD_REQUEST.value(), "남성만 신청 가능한 경기 입니다."),

  // 친구
  NOT_SELF_FRIEND(HttpStatus.BAD_REQUEST.value(), "자기 자신을 친구 신청 할수 없습니다."),
  NOT_SELF_APPLY(HttpStatus.BAD_REQUEST.value(), "자기 자신이 한 친구 신청만 취소 할수 있습니다."),
  ALREADY_APPLY_ACCEPT_STATUS(HttpStatus.BAD_REQUEST.value(), "이미 친구 신청 햇거나 "
      + "수락한 상태 입니다."),
  OTHER_FRIEND_FULL(HttpStatus.BAD_REQUEST.value(), "친구 추가하려는 상대방이 친구가 다 "
      + "찼습니다."),
  SELF_FRIEND_FULL(HttpStatus.BAD_REQUEST.value(), "친구 목록이 다 찼습니다."),
  NOT_FOUND_APPLY_FRIEND(HttpStatus.BAD_REQUEST.value(), "친구 신청한 상태가 아닙니다."),

  // 서버 오류
  INTERNAL_SERVER_ERROR(HttpStatus.BAD_REQUEST.value(),"내부 서버 오류");

  private final int statusCode;
  private final String description;
}
