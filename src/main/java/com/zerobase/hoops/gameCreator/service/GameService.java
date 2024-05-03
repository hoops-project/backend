package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.exception.ErrorCode.ALREADY_GAME_CREATED;
import static com.zerobase.hoops.exception.ErrorCode.GAME_NOT_FOUND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_AFTER_THIRTY_MINUTE;
import static com.zerobase.hoops.exception.ErrorCode.NOT_DELETE_STARTDATE;
import static com.zerobase.hoops.exception.ErrorCode.NOT_GAME_CREATOR;
import static com.zerobase.hoops.exception.ErrorCode.NOT_UPDATE_HEADCOUNT;
import static com.zerobase.hoops.exception.ErrorCode.NOT_UPDATE_MAN;
import static com.zerobase.hoops.exception.ErrorCode.NOT_UPDATE_WOMAN;
import static com.zerobase.hoops.exception.ErrorCode.USER_NOT_FOUND;
import static com.zerobase.hoops.gameCreator.type.Gender.FEMALEONLY;
import static com.zerobase.hoops.gameCreator.type.Gender.MALEONLY;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.APPLY;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.DELETE;
import static com.zerobase.hoops.users.type.GenderType.FEMALE;
import static com.zerobase.hoops.users.type.GenderType.MALE;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateResponse;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.util.Util;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.GenderType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GameService {

  private final GameRepository gameRepository;

  private final ParticipantGameRepository participantGameRepository;

  private final UserRepository userRepository;

  private final TokenProvider tokenProvider;

  /**
   * 경기 생성
   */
  public CreateResponse createGame(CreateRequest request, String token) throws Exception {
    log.info("createGame start");
    // 이메일로 유저 조회
    String email = this.tokenProvider.parseClaims(token.substring(7)).getSubject();

    var user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    /**
     * 예) 입력한 주소 : 서울 마포구 와우산로13길 6 지하1,2층
     *    입력한 경기 시작 시간 : 2024-05-02T07:00:00
     *    이를 기준으로 기존에 있던 경기 개수를 찾습니다.
     *    주어진 시작 시간에서 30분 전부터 30분 후까지의 시간 범위를 계산하면
     *    2024-05-02T06:30:00 ~ 2024-05-02T07:30:00 까지 입니다.
     *    이 기간 동안 해당 주소에서 예정된 경기를 찾습니다.
     **/
    LocalDateTime startDatetime = request.getStartDateTime();
    LocalDateTime beforeDatetime = startDatetime.minusMinutes(30);
    LocalDateTime afterDateTime = startDatetime.plusMinutes(30);
    LocalDateTime nowDateTime = LocalDateTime.now();

    Long aroundGameCount = this.gameRepository
        .countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull
            (beforeDatetime, afterDateTime, request.getAddress())
        .orElse(0L);

    /**
     * 주어진 시작 시간에서 30분 전부터 30분 후까지의 시간 범위를 계산해
     * 시간 범위에 해당하는 해당 주소에서 예정된 경기를 못찾을시
     */
     if(aroundGameCount == 0) {
       /**
        *  예) 현재 시간 : 2024-05-02T06:50:00
        *      입력한 경기 시작 시간 : 2024-05-02T07:00:00
        *      2024-05-02T06:30:00 보다 2024-05-02T06:50:00 이후 이므로
        *      Exception 발생
        */
      if(beforeDatetime.isBefore(nowDateTime)) {
        throw new CustomException(NOT_AFTER_THIRTY_MINUTE);
      }
    } else { // 시간 범위에 해당하는 해당 주소에서 예정된 경기를 찾을시
       /**
        *   시간 범위에 해당하는 해당 주소에서 이미 열린 경기가 있으므로
        *   Exception 발생
        */
      throw new CustomException(ALREADY_GAME_CREATED);
    }

    // CREATOR 확인
    boolean creatorFlag = false;
    List<String> roles = user.getRoles();
    for(String role : roles) {
      if(role.equals("ROLE_CREATOR")) {
        creatorFlag = true;
        break;
      }
    }

    // 없으면 CREATOR 추가
    if(!creatorFlag) {
      roles.add("ROLE_CREATOR");
      user.setRoles(roles);
      this.userRepository.save(user);
    }

    // 경기 생성
    GameEntity gameEntity = CreateRequest.toEntity(request, user);
    gameEntity.setCityName(Util.getCityName(request.getAddress()));

    this.gameRepository.save(gameEntity);

    log.info("createGame end");

    return CreateResponse.toDto(gameEntity);
  }

  /**
   * 경기 수정
   */
  public UpdateResponse updateGame(UpdateRequest request, String token) throws Exception {
    log.info("updateGame start");

    // 이메일로 유저 조회
    String email = this.tokenProvider.parseClaims(token.substring(7)).getSubject();

    var user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    // 게임 아이디로 게임 조회, 먼저 삭제 되었는지 조회
    var game =
        this.gameRepository.findByGameIdAndDeletedDateTimeNull(request.getGameId())
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    //자신이 경기 개최자가 아니면 수정 못하게
    if(user.getUserId() != game.getUserEntity().getUserId()) {
      throw new CustomException(NOT_GAME_CREATOR);
    }

    /**
     * 예) 수정전 주소 : 서울 마포구 와우산로13길 6 지하1,2층
     *     수정전 경기 시작 시간 : 2024-05-02T07:00:00
     *     수정 하려는 주소 : 서울 마포구 와우산로13길 6 지하1,2층
     *     수정 하려는 경기 시작 시간 : 2024-05-02T07:00:00
     *     수정 하려는 시작 시간에서 30분 전부터 30분 후까지의 시간 범위를 계산하면
     *     2024-05-02T06:30:00 ~ 2024-05-02T07:30:00 까지 입니다.
     * 주의) 이 기간 동안 해당 주소에서 예정된 경기를 찾는데
     *      수정 전 경기는 DB에 들어가 있으므로
     *      수정 전 경기는 제외 하고 예정된 경기를 찾음
     */
    LocalDateTime startDatetime = request.getStartDateTime();
    LocalDateTime beforeDatetime = startDatetime.minusMinutes(30);
    LocalDateTime afterDateTime = startDatetime.plusMinutes(30);
    LocalDateTime nowDateTime = LocalDateTime.now();

    Long aroundGameCount = this.gameRepository
        .countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot
            (beforeDatetime, afterDateTime, request.getAddress(), request.getGameId())
        .orElse(0L);

    /**
     * 주어진 시작 시간에서 30분 전부터 30분 후까지의 시간 범위를 계산해
     * 시간 범위에 해당하는 해당 주소에서 예정된 경기를 못찾을시
     */
    if(aroundGameCount == 0) {
      /**
       *  예) 현재 시간 : 2024-05-02T06:50:00
       *      수정하려는 경기 시작 시간 : 2024-05-02T07:00:00
       *      2024-05-02T06:30:00 보다 2024-05-02T06:50:00 이후 이므로
       *      Exception 발생
       */
      if(beforeDatetime.isBefore(nowDateTime)) {
        throw new CustomException(NOT_AFTER_THIRTY_MINUTE);
      }
    } else { // 시간 범위에 해당하는 해당 주소에서 예정된 경기를 찾을시
      /**
       *   시간 범위에 해당하는 해당 주소에서 이미 열린 경기가 있으므로
       *   Exception 발생
       */
      throw new CustomException(ALREADY_GAME_CREATED);
    }

    /**
     * 예) 변경 하려는 인원수 : 6
     *     현재 경기에 수락된 인원수 : 8
     *     이 경우 Exception 발생
     */
    Long headCount =
        this.participantGameRepository.countByStatusAndGameEntityGameId
            (ACCEPT, request.getGameId())
            .orElse(0L);

    if(request.getHeadCount() < headCount) {
      throw new CustomException(NOT_UPDATE_HEADCOUNT);
    }

    /**
     * 수정하려는 성별이 ALL 이면 이 메서드 통과
     */
    Gender gender = request.getGender();
    if(gender == MALEONLY || gender == FEMALEONLY) {
      GenderType queryGender = gender == MALEONLY ? FEMALE : MALE;

      /**
       * 예) 수정하려는 성별 : MALEONLY -> queryGender : FEMALE
       *     경기에 수락된 인원들중 FEMALE 갯수를 검사
       */
      Long count = this.participantGameRepository
          .countByStatusAndGameEntityGameIdAndUserEntityGender
          (ACCEPT, request.getGameId(), queryGender)
          .orElse(0L);

      log.info(count.toString());

      /**
       * 예) 수정하려는 성별 : MALEONLY
       *     경기에 수락된 인원들중 FEMALE 갯수를 검사
       *     FEMALE이 한명이라도 있으면 안되므로 Exception 발생
       */
      if(count >= 1) {
        if(gender == MALEONLY) {
          throw new CustomException(NOT_UPDATE_MAN);
        } else {
          throw new CustomException(NOT_UPDATE_WOMAN);
        }
      }
    }

    // 경기 수정
    GameEntity gameEntity = UpdateRequest.toEntity(request, game);
    this.gameRepository.save(gameEntity);

    log.info("updateGame end");

    return UpdateResponse.toDto(gameEntity);
  }

  /**
   * 경기 삭제
   */
  public DeleteResponse delete(DeleteRequest request, String token) throws Exception {
    log.info("deleteGame start");

    // 이메일로 유저 조회
    String email = this.tokenProvider.parseClaims(token.substring(7)).getSubject();

    var user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    // 경기 아이디로 게임 조회, 먼저 삭제 되었는지 조회
    var game = this.gameRepository.findByGameIdAndDeletedDateTimeNull(request.getGameId())
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    // CREATOR 판별
    boolean creatorFlag = false;

    for(String role : user.getRoles()) {
      if(role.equals("ROLE_CREATOR")) {
        creatorFlag = true;
        break;
      }
    }

    // CREATOR 일때 관리자 일때는 PASS
    if(creatorFlag) {
      // 자신이 경기 개최자가 아니면 삭제 못하게
      if(user.getUserId() != game.getUserEntity().getUserId()) {
        throw new CustomException(NOT_GAME_CREATOR);
      }

      // 설정한 경기 시작 30분 전에만 삭제 가능
      LocalDateTime beforeDatetime = game.getStartDateTime().minusMinutes(30);
      LocalDateTime nowDateTime = LocalDateTime.now();

      if(nowDateTime.isAfter(beforeDatetime)) {
        throw new CustomException(NOT_DELETE_STARTDATE);
      }
    }

    // 경기 삭제 전에 기존에 경기에 ACCEPT, APPLY 멤버들 다 DELETE
    List<ParticipantGameEntity> participantGameEntityList =
        this.participantGameRepository.findByStatusInAndGameEntityGameId
            (List.of(ACCEPT, APPLY), request.getGameId());

    if(!participantGameEntityList.isEmpty()) {
      for(ParticipantGameEntity entity : participantGameEntityList) {
        entity.setStatus(DELETE);
        entity.setDeletedDateTime(LocalDateTime.now());
        this.participantGameRepository.save(entity);
      }
    }

    // 경기 삭제
    GameEntity gameEntity = DeleteRequest.toEntity(game);
    this.gameRepository.save(gameEntity);

    // 경기 삭제후 경기 개설한 것이 없다면 CREATOR 제거
    if(creatorFlag) {
      Long gameCreateCount =
          this.gameRepository.countByDeletedDateTimeNullAndUserEntityUserId(user.getUserId())
              .orElse(0L);

      if(gameCreateCount == 0) {
        List<String> roles = user.getRoles();
        roles.remove("ROLE_CREATOR");
        user.setRoles(roles);
        this.userRepository.save(user);
      }
    }

    log.info("deleteGame end");

    return DeleteResponse.toDto(gameEntity);
  }
}
