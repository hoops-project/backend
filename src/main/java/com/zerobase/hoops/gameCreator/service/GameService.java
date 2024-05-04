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
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
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
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.GenderType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
  public CreateResponse createGame(CreateRequest request, String token)
      throws Exception {
    log.info("createGame start");
    // 아이디로 유저 조회
    var objectId = tokenProvider.parseClaims(token.substring(7)).get("id");

    String id = String.valueOf(objectId);

    var user = userRepository.findByIdAndDeleteDateTimeNull(id)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    validationCreateGame(request);

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
      userRepository.save(user);
    }

    // 경기 생성
    GameEntity gameEntity = CreateRequest.toEntity(request, user);

    gameRepository.save(gameEntity);

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

    long aroundGameCount = this.gameRepository
        .countByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull
            (beforeDatetime, afterDateTime, request.getAddress())
        .orElse(0L);

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
   * 경기 상세 조회
   */
  public DetailResponse getGameDetail(Long gameId) {
    var game = this.gameRepository.findByGameIdAndDeletedDateTimeNull(gameId)
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    return DetailResponse.toDto(game);
  }

  /**
   * 경기 수정
   */
  public UpdateResponse updateGame(UpdateRequest request, String token)
      throws Exception {
    log.info("updateGame start");

    // 아이디로 유저 조회
    var objectId = tokenProvider.parseClaims(token.substring(7)).get("id");

    String id = String.valueOf(objectId);

    var user = this.userRepository.findByIdAndDeleteDateTimeNull(id)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    // 게임 아이디로 게임 조회, 먼저 삭제 되었는지 조회
    var game =
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
                request.getGameId())
        .orElse(0L);

    /**
     * 주어진 시작 시간에서 30분 전부터 30분 후까지의 시간 범위를 계산해
     * 시간 범위에 해당하는 해당 주소에서 예정된 경기를 못찾을시
     */
    if (aroundGameCount == 0) {
      /**
       *  예) 현재 시간 : 2024-05-02T06:50:00
       *      수정하려는 경기 시작 시간 : 2024-05-02T07:00:00
       *      2024-05-02T06:30:00 보다 2024-05-02T06:50:00 이후 이므로
       *      Exception 발생
       */
      if (beforeDatetime.isBefore(nowDateTime)) {
        throw new CustomException(NOT_AFTER_THIRTY_MINUTE);
      }
    } else { // 시간 범위에 해당하는 해당 주소에서 예정된 경기를 찾을시
      throw new CustomException(ALREADY_GAME_CREATED);
    }

    /**
     * 예) 변경 하려는 인원수 : 6
     *     현재 경기에 수락된 인원수 : 8
     *     이 경우 Exception 발생
     */
    long headCount =
        participantGameRepository.countByStatusAndGameEntityGameId
                (ACCEPT, request.getGameId())
            .orElse(0L);

    if (request.getHeadCount() < headCount) {
      throw new CustomException(NOT_UPDATE_HEADCOUNT);
    }

    // 수정하려는 성별이 ALL 이면 이 메서드 통과
    Gender gender = request.getGender();
    if (gender == MALEONLY || gender == FEMALEONLY) {
      GenderType queryGender = gender == MALEONLY ? FEMALE : MALE;

      long count = this.participantGameRepository
          .countByStatusAndGameEntityGameIdAndUserEntityGender
              (ACCEPT, request.getGameId(), queryGender)
          .orElse(0L);
      /**
       * 예) 수정하려는 성별 : MALEONLY
       *     경기에 수락된 인원들중 FEMALE 갯수를 검사
       *     FEMALE이 한명이라도 있으면 안되므로 Exception 발생
       */
      if (count >= 1) {
        if (gender == MALEONLY) {
          throw new CustomException(NOT_UPDATE_MAN);
        } else {
          throw new CustomException(NOT_UPDATE_WOMAN);
        }
      }
    }
  }

  /**
   * 경기 삭제
   */
  public DeleteResponse delete(DeleteRequest request, String token)
      throws Exception {
    log.info("deleteGame start");

    // 아이디로 유저 조회
    var objectId = tokenProvider.parseClaims(token.substring(7)).get("id");

    String id = String.valueOf(objectId);

    var user = userRepository.findByIdAndDeleteDateTimeNull(id)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    // 경기 아이디로 게임 조회, 먼저 삭제 되었는지 조회
    var game = this.gameRepository.findByGameIdAndDeletedDateTimeNull(request.getGameId())
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    // CREATOR 판별
    boolean creatorFlag = false;

    for (String role : user.getRoles()) {
      if (role.equals("ROLE_CREATOR")) {
        creatorFlag = true;
        break;
      }
    }

    validationDeleteGame(user, game, creatorFlag);

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

    // 경기 삭제후 경기 개설한 것이 없다면 CREATOR 제거
    if(creatorFlag) {
      long gameCreateCount =
          gameRepository.countByDeletedDateTimeNullAndUserEntityUserId(user.getUserId())
              .orElse(0L);

      if (gameCreateCount == 0) {
        List<String> roles = user.getRoles();
        roles.remove("ROLE_CREATOR");
        user.setRoles(roles);
        userRepository.save(user);
      }
    }

    log.info("deleteGame end");

    return DeleteResponse.toDto(gameEntity);

  }

  /**
   * 경기 삭제 전 validation 체크
   */
  private void validationDeleteGame(UserEntity user, GameEntity game, boolean creatorFlag) {
    // CREATOR 일때 관리자 일때는 PASS
    if(creatorFlag) {
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


}
