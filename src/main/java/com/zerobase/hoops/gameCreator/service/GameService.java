package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.exception.ErrorCode.ALREADY_GAME_CREATED;
import static com.zerobase.hoops.exception.ErrorCode.GAME_NOT_FOUND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_AFTER_THIRTY_MINUTE;
import static com.zerobase.hoops.exception.ErrorCode.NOT_CREATE_FIVEONFIVE;
import static com.zerobase.hoops.exception.ErrorCode.NOT_CREATE_THREEONTHREEE;
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
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateResponse;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.GenderType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GameService {

  private final GameRepository gameRepository;

  private final ParticipantGameRepository participantGameRepository;

  private final UserRepository userRepository;

  private final JwtTokenExtract jwtTokenExtract;

  private static UserEntity user;

  /**
   * 경기 생성
   */
  public CreateResponse createGame(CreateRequest request) {
    log.info("createGame start");

    Long userId = jwtTokenExtract.currentUser().getUserId();

    user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    validationCreateGame(request);

    // 경기 생성
    GameEntity gameEntity = CreateRequest.toEntity(request, user);

    gameRepository.save(gameEntity);

    // 경기 개설자는 경기에 참가인 상태로 있어야 함
    ParticipantGameEntity participantGameEntity =
        ParticipantGameEntity.toGameCreatorEntity(gameEntity, user);

    participantGameRepository.save(participantGameEntity);

    log.info("createGame end");

    return CreateResponse.toDto(gameEntity);
  }

  /**
   * 경기 생성 전 validation 체크
   */
  private void validationCreateGame(CreateRequest request) {
    /**
     *    주어진 시작 시간에서 30분 전부터 30분 후까지의 시간 범위를 계산하여
     *    이 기간 동안 해당 주소에서 예정된 경기를 찾습니다.
     **/
    LocalDateTime startDatetime = request.getStartDateTime();
    LocalDateTime beforeDatetime = startDatetime.minusMinutes(30);
    LocalDateTime afterDateTime = startDatetime.plusMinutes(30);
    LocalDateTime nowDateTime = LocalDateTime.now();

    long aroundGameCount = gameRepository
        .countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull
            (beforeDatetime, afterDateTime, request.getAddress());

    validationCommon(aroundGameCount, beforeDatetime, nowDateTime,
        request.getHeadCount(), request.getMatchFormat());
  }



  /**
   * 경기 상세 조회
   */
  public DetailResponse getGameDetail(Long gameId) {
    GameEntity game = gameRepository.findByGameIdAndDeletedDateTimeNull(gameId)
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));
    return DetailResponse.toDto(game);
  }

  /**
   * 경기 수정
   */
  public UpdateResponse updateGame(UpdateRequest request) {
    log.info("updateGame start");

    Long userId = jwtTokenExtract.currentUser().getUserId();

    user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    // 게임 아이디로 게임 조회, 먼저 삭제 되었는지 조회
    GameEntity game =
        gameRepository.findByGameIdAndDeletedDateTimeNull(request.getGameId())
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    validationUpdateGame(request, user, game);

    // 경기 수정
    GameEntity gameEntity = UpdateRequest.toEntity(request, game);
    gameRepository.save(gameEntity);

    log.info("updateGame end");

    return UpdateResponse.toDto(gameEntity);
  }

  /**
   * 경기 수정 전 validation 체크
   */
  private void validationUpdateGame(UpdateRequest request, UserEntity user, GameEntity game) {
    //자신이 경기 개최자가 아니면 수정 못하게
    if(!Objects.equals(user.getUserId(), game.getUserEntity().getUserId())) {
      throw new CustomException(NOT_GAME_CREATOR);
    }

    /**
     *   수정 하려는 시작 시간에서 30분 전부터 30분 후까지의 시간 범위를 계산하면
     *   2024-05-02T06:30:00 ~ 2024-05-02T07:30:00 까지 입니다.
     *   이 기간 동안 해당 주소에서 예정된 경기를 찾는데 수정 전 경기는 제외
     */
    LocalDateTime startDatetime = request.getStartDateTime();
    LocalDateTime beforeDatetime = startDatetime.minusMinutes(30);
    LocalDateTime afterDateTime = startDatetime.plusMinutes(30);
    LocalDateTime nowDateTime = LocalDateTime.now();

    long aroundGameCount = gameRepository
        .countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot
            (beforeDatetime, afterDateTime, request.getAddress(),
                request.getGameId());


    validationCommon(aroundGameCount, beforeDatetime, nowDateTime,
        request.getHeadCount(), request.getMatchFormat());

    /**
     * 예) 변경 하려는 인원수 : 6
     *     현재 경기에 수락된 인원수 : 8
     *     이 경우 Exception 발생
     */
    long headCount =
        participantGameRepository.countByStatusAndGameEntityGameId
            (ACCEPT, request.getGameId());

    if (request.getHeadCount() < headCount) {
      throw new CustomException(NOT_UPDATE_HEADCOUNT);
    }

    // 수정하려는 성별이 ALL 이면 이 메서드 통과
    Gender gender = request.getGender();
    if (gender == MALEONLY || gender == FEMALEONLY) {
      GenderType queryGender = gender == MALEONLY ? FEMALE : MALE;

      long genderCount = participantGameRepository
          .countByStatusAndGameEntityGameIdAndUserEntityGender
              (ACCEPT, request.getGameId(), queryGender);

      /**
       * 예) 수정하려는 성별 : MALEONLY
       *     경기에 수락된 인원들중 FEMALE 갯수를 검사
       *     FEMALE이 한명이라도 있으면 안되므로 Exception 발생
       */
      if (genderCount >= 1) {
        if (gender == MALEONLY) {
          throw new CustomException(NOT_UPDATE_MAN);
        } else {
          throw new CustomException(NOT_UPDATE_WOMAN);
        }
      }
    }

  }

  /**
   * 경기 생성, 수정 공통 validation
   */
  private void validationCommon(long aroundGameCount,
      LocalDateTime beforeDatetime, LocalDateTime nowDateTime,
      Long headCount, MatchFormat matchFormat) {

    // 3:3 매치 일때 6명 ~ 9명 설정 가능
    if(matchFormat == MatchFormat.THREEONTHREE) {
      if(headCount < 6 || headCount > 9) {
        throw new CustomException(NOT_CREATE_THREEONTHREEE);
      }
    } else if(matchFormat == MatchFormat.FIVEONFIVE) { // 5:5 매치 10 ~ 15명
      if(headCount < 10 || headCount > 15) {
        throw new CustomException(NOT_CREATE_FIVEONFIVE);
      }
    }

    /**
     * 주어진 시작 시간에서 30분 전부터 30분 후까지의 시간 범위를 계산해
     * 시간 범위에 해당하는 해당 주소에서 예정된 경기를 못찾을시
     */
    if(aroundGameCount == 0) {
      // 입력한 경기 시작 시간은 현재시간 30분 보다 후 여야 함
      if(beforeDatetime.isBefore(nowDateTime)) {
        throw new CustomException(NOT_AFTER_THIRTY_MINUTE);
      }
    } else { // 시간 범위에 해당하는 해당 주소에서 예정된 경기를 찾을시
      throw new CustomException(ALREADY_GAME_CREATED);
    }
  }

  /**
   * 경기 삭제
   */
  public DeleteResponse deleteGame(DeleteRequest request) {
    log.info("deleteGame start");

    Long userId = jwtTokenExtract.currentUser().getUserId();

    user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    // 경기 아이디로 게임 조회, 먼저 삭제 되었는지 조회
    GameEntity game = gameRepository.findByGameIdAndDeletedDateTimeNull(request.getGameId())
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    // USER 판별
    boolean userFlag = false;

    for (String role : user.getRoles()) {
      if (role.equals("ROLE_USER")) {
        userFlag = true;
        break;
      }
    }

    // USER 일때
    // 관리자 일때는 PASS
    if(userFlag) {
      validationDeleteGame(user, game);
    }

    // 경기 삭제 전에 기존에 경기에 ACCEPT, APPLY 멤버들 다 DELETE
    List<ParticipantGameEntity> participantGameEntityList =
        participantGameRepository.findByStatusInAndGameEntityGameId
            (List.of(ACCEPT, APPLY), request.getGameId());

    if (!participantGameEntityList.isEmpty()) {
      for (ParticipantGameEntity entity : participantGameEntityList) {
        entity.setStatus(DELETE);
        entity.setDeletedDateTime(LocalDateTime.now());
        participantGameRepository.save(entity);
      }
    }

    // 경기 삭제
    GameEntity gameEntity = DeleteRequest.toEntity(game);
    gameRepository.save(gameEntity);

    log.info("deleteGame end");

    return DeleteResponse.toDto(gameEntity);
  }

  /**
   * 경기 삭제 전 validation 체크
   */
  private void validationDeleteGame(UserEntity user, GameEntity game) {
    // 자신이 경기 개최자가 아니면 삭제 못하게
    if(!Objects.equals(user.getUserId(), game.getUserEntity().getUserId())) {
      throw new CustomException(NOT_GAME_CREATOR);
    }

    // 설정한 경기 시작 30분 전에만 삭제 가능
    LocalDateTime beforeDatetime = game.getStartDateTime().minusMinutes(30);
    LocalDateTime nowDateTime = LocalDateTime.now();

    if(nowDateTime.isAfter(beforeDatetime)) {
      throw new CustomException(NOT_DELETE_STARTDATE);
    }
  }


}
