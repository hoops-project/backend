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
  WRONG_EMAIL(HttpStatus.NO_CONTENT.value(), "해당 이메일로 발송된 인증번호가 없습니다."),
  NOT_MATCHED_NUMBER(HttpStatus.FORBIDDEN.value(), "인증번호가 일치하지 않습니다."),
  USER_NOT_CONFIRM(HttpStatus.BAD_REQUEST.value(), "인증되지 않은 회원입니다."),

  NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED.value(), "토큰 형식의 값을 찾을 수 없습니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "기간이 만료된 토큰입니다."),
  EXPIRED_REFRESH_TOKEN(HttpStatus.FORBIDDEN.value(), "리프레시 토큰의 기간이 만료되었습니다."),
  NOT_MATCHED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "토큰 정보가 일치하지 않습니다."),
  UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "지원하지 않는 토큰입니다."),
  ACCESS_DENIED(HttpStatus.UNAUTHORIZED.value(), "알 수 없는 이유로 요청이 거절되었습니다."),
  ALREADY_LOGOUT(HttpStatus.BAD_REQUEST.value(), "이미 로그아웃한 회원입니다."),

  DUPLICATED_ID(HttpStatus.CONFLICT.value(), "이미 사용 중인 아이디입니다."),
  DUPLICATED_NICKNAME(HttpStatus.CONFLICT.value(), "이미 사용 중인 별명입니다."),
  DUPLICATED_EMAIL(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "아이디가 존재하지 않습니다."),
  NOT_MATCHED_PASSWORD(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다."),

  // 블랙리스트
  BAN_FOR_10DAYS(HttpStatus.BAD_REQUEST.value(), "10일 후에 다시 이용 가능합니다."),
  ALREADY_BLACKLIST(HttpStatus.BAD_REQUEST.value(), "이미 블랙리스트로 처리 되었습니다."),
  NOT_BLACKLIST(HttpStatus.BAD_REQUEST.value(), "블랙리스트 목록에 없습니다."),

  // 게임 개설자
  GAME_NOT_FOUND(HttpStatus.NOT_FOUND.value(),  "경기를 찾을 수 없습니다."),
  NOT_GAME_CREATOR(HttpStatus.NOT_ACCEPTABLE.value(), "경기 개최자가 아닙니다."),
  NOT_UPDATE_HEADCOUNT(HttpStatus.BAD_REQUEST.value(),  "설정 하려는 인원수가 수락된 인원수 보다 적습니다."),
  NOT_UPDATE_MAN(HttpStatus.BAD_REQUEST.value(), "여자가 그룹에 참가중입니다."),
  NOT_UPDATE_WOMAN(HttpStatus.BAD_REQUEST.value(), "남자가 그룹에 참가중입니다."),
  NOT_AFTER_THIRTY_MINUTE(HttpStatus.BAD_REQUEST.value(), "경기 시작 시간은 현재 시간으로부터 "
      + "최소 30분 이후여야 합니다."),
  ALREADY_GAME_CREATED(HttpStatus.BAD_REQUEST.value(), "설정한 경기 시작 시간 1시간 전부터 "
      + "1시간 후까지(정각 제외) 이미 열린 경기가 있습니다."),
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
  DUPLICATED_TRY_TO_JOIN_GAME(HttpStatus.BAD_REQUEST.value(), "경기 신청이 이미 등록되어 있습니다."),
  ALREADY_PARTICIPANT_GAME(HttpStatus.BAD_REQUEST.value(), "이미 경기에 참가 해 있습니다."),
  NOT_PARTICIPANT_GAME(HttpStatus.BAD_REQUEST.value(), "경기에 참가해 있지 않습니다."),


  // 친구
  NOT_SELF_FRIEND(HttpStatus.BAD_REQUEST.value(), "자기 자신을 친구 신청 할수 없습니다."),
  NOT_SELF_APPLY(HttpStatus.BAD_REQUEST.value(), "자기 자신이 한 친구 신청만 취소 할수 있습니다."),
  NOT_MY_RECEIVE(HttpStatus.BAD_REQUEST.value(), "자신이 받은 친구 신청만 수락 할수 있습니다."),
  NOT_SELF_ACCEPT(HttpStatus.BAD_REQUEST.value(), "자신이 받은 친구만 삭제 할수 있습니다."),
  ALREADY_APPLY_ACCEPT_STATUS(HttpStatus.BAD_REQUEST.value(), "이미 친구 신청 햇거나 "
      + "수락한 상태 입니다."),
  OTHER_FRIEND_FULL(HttpStatus.BAD_REQUEST.value(), "친구 추가하려는 상대방이 친구가 다 "
      + "찼습니다."),
  SELF_FRIEND_FULL(HttpStatus.BAD_REQUEST.value(), "나의 친구가 다 찼습니다."),
  NOT_FOUND_APPLY_FRIEND(HttpStatus.BAD_REQUEST.value(), "친구 신청한 상태가 아닙니다."),
  NOT_FOUND_ACCEPT_FRIEND(HttpStatus.BAD_REQUEST.value(), "친구 수락한 상태가 아닙니다."),
  NOT_FOUND_NICKNAME(HttpStatus.BAD_REQUEST.value(), "닉네임은 필수 값 입니다."),

  // 매너점수
  INVALID_GAME_ID(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 game_id입니다."),
  EXIST_RATE(HttpStatus.BAD_REQUEST.value(), "이미 평가가 완료되었습니다."),

  // 초대
  ALREADY_INVITE_GAME(HttpStatus.BAD_REQUEST.value(), "이미 경기에 초대 돼 있습니다."),
  NOT_INVITE_FOUND(HttpStatus.BAD_REQUEST.value(), "해당 경기에 초대 요청 상태가 아닙니다."),
  NOT_SELF_REQUEST(HttpStatus.BAD_REQUEST.value(), "본인이 요청한 경기 초대만 취소 할수 있습니다"
      + "."),
  NOT_SELF_INVITE_REQUEST(HttpStatus.BAD_REQUEST.value(), "본인이 받은 경기 초대만 수락,"
      + "거절 할수 있습니다."),
  NOT_GAME_INVITE(HttpStatus.BAD_REQUEST.value(), "해당 경기가 초대 불가능입니다."),

  // 신고
  AlREADY_REPORTED(HttpStatus.BAD_REQUEST.value(), "이미 신고가 완료되었습니다."),
  NOT_EXIST_REPORTED(HttpStatus.BAD_REQUEST.value(), "신고 내용이 없습니다."),

  // 서버 오류
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),"내부 서버 오류"),

  // 채팅
  NOT_ACCEPT_USER_FOR_GAME(HttpStatus.BAD_REQUEST.value(), "경기 개최자가 승인한 유저만 채팅에 참여할 수 있습니다."),
  NOT_EXIST_CHATROOM(HttpStatus.BAD_REQUEST.value(), "해당하는 채팅룸이 없습니다."),
  NOT_EXIST_MESSAGE_FOR_CHATROOM(HttpStatus.BAD_REQUEST.value(), "메세지 전송을 위한 채팅룸이 없습니다."),
  NOT_EXIST_CHATROOM_SESSION(HttpStatus.BAD_REQUEST.value(), "채팅룸의 sessionId가 없습니다."),
  NOT_EXIST_USER_SESSION(HttpStatus.BAD_REQUEST.value(), "User Session이 없습니다"),
  CANNOT_ENTER_CHAT(HttpStatus.BAD_REQUEST.value(), "채팅에 참가할 수 없습니다.");

  private final int statusCode;
  private final String description;
}
