package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.exception.ErrorCode.ALREADY_GAME_CREATED;
import static com.zerobase.hoops.exception.ErrorCode.GAME_NOT_FOUND;
import static com.zerobase.hoops.exception.ErrorCode.NOT_AFTER_THIRTY_MINUTE;
import static com.zerobase.hoops.exception.ErrorCode.NOT_CREATE_FIVEONFIVE;
import static com.zerobase.hoops.exception.ErrorCode.NOT_CREATE_THREEONTHREEE;
import static com.zerobase.hoops.exception.ErrorCode.NOT_DELETE_STARTDATE;
import static com.zerobase.hoops.exception.ErrorCode.NOT_GAME_CREATOR;
import static com.zerobase.hoops.exception.ErrorCode.NOT_PARTICIPANT_FOUND;
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
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteGameResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.ParticipantUser;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.WithDrawGameResponse;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.invite.repository.InviteRepository;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
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

  private final InviteRepository inviteRepository;

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
    LocalDateTime startDatetime = request.getStartDateTime(); // 예: 14:00
    LocalDateTime beforeDatetime = startDatetime.minusHours(1).plusSeconds(1); // 13:00:01
    LocalDateTime afterDateTime = startDatetime.plusHours(1).minusSeconds(1); // 14:59:59
    LocalDateTime nowDateTime = LocalDateTime.now();

    // 13:00:01부터 14:59:59까지의 기간 동안 동일한 주소에서 예정된 경기 수를 조회합니다.
    boolean gameExists = gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull
            (beforeDatetime, afterDateTime, request.getAddress());

    validationCommon(gameExists, nowDateTime, startDatetime,
        request.getHeadCount(), request.getMatchFormat());
  }



  /**
   * 경기 상세 조회
   */
  public DetailResponse getGameDetail(Long gameId) {
    GameEntity game = gameRepository.findByGameIdAndDeletedDateTimeNull(gameId)
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    List<ParticipantGameEntity> participantGameEntityList =
        participantGameRepository
            .findByGameEntityGameIdAndStatusAndDeletedDateTimeNull
                (gameId, ACCEPT);

    List<ParticipantUser> participantUserList =
        participantGameEntityList.stream().map(ParticipantUser::toDto).toList();

    return DetailResponse.toDto(game, participantUserList);
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

    LocalDateTime startDatetime = request.getStartDateTime(); // 예: 14:00
    LocalDateTime beforeDatetime = startDatetime.minusHours(1).plusSeconds(1); // 13:00:01
    LocalDateTime afterDateTime = startDatetime.plusHours(1).minusSeconds(1); // 14:59:59
    LocalDateTime nowDateTime = LocalDateTime.now();

    boolean gameExists = gameRepository
        .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndGameIdNot
            (beforeDatetime, afterDateTime, request.getAddress(),
                request.getGameId());


    validationCommon(gameExists, nowDateTime, startDatetime,
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
  private void validationCommon(boolean gameExists, LocalDateTime nowDateTime,
      LocalDateTime startDateTime,
      Long headCount, MatchFormat matchFormat) {

    LocalDateTime beforeDateTime = startDateTime.minusMinutes(30);

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

    // 시간 범위에 해당하는 해당 주소에서 예정된 경기를 찾을시
    if(gameExists) {
      throw new CustomException(ALREADY_GAME_CREATED);
    } else { // 예정된 경기를 못찾을시
      // 입력한 경기 시작 시간은 현재시간 30분 보다 후 여야 함
      if(beforeDateTime.isBefore(nowDateTime)) {
        throw new CustomException(NOT_AFTER_THIRTY_MINUTE);
      }
    }
  }

  /**
   * 경기 삭제 분기
   */
  public Object delete(DeleteRequest request) {
    Long userId = jwtTokenExtract.currentUser().getUserId();

    user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    // 경기 아이디로 게임 조회, 먼저 삭제 되었는지 조회
    GameEntity game = gameRepository.findByGameIdAndDeletedDateTimeNull(request.getGameId())
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    if(Objects.equals(user.getUserId(), game.getUserEntity().getUserId())) {
      return deleteGame(game);
    } else { // 자신이 경기 개최자가 아닌 팀원 이라면
      return withdrewGame(game);
    }

  }

  /**
   * 경기 삭제
   */
  private DeleteGameResponse deleteGame(GameEntity game) {

    // 설정한 경기 시작 30분 전에만 삭제 가능
    LocalDateTime beforeDatetime = game.getStartDateTime().minusMinutes(30);
    LocalDateTime nowDateTime = LocalDateTime.now();

    if(nowDateTime.isAfter(beforeDatetime)) {
      throw new CustomException(NOT_DELETE_STARTDATE);
    }

    // 경기 삭제 전에 기존에 경기에 ACCEPT, APPLY 멤버들 다 DELETE
    List<ParticipantGameEntity> participantGameEntityList =
        participantGameRepository.findByStatusInAndGameEntityGameId
            (List.of(ACCEPT, APPLY), game.getGameId());

    participantGameEntityList.forEach(participantGame -> {
      participantGame.setStatus(DELETE);
      participantGame.setDeletedDateTime(LocalDateTime.now());
      participantGameRepository.save(participantGame);
    });

    // 해당 경기에 초대 신청된 것들 다 CANCEL
    List<InviteEntity> inviteEntityList = inviteRepository
        .findByInviteStatusAndGameEntityGameId
            (InviteStatus.REQUEST, game.getGameId());

    inviteEntityList.forEach(invite -> {
      invite.setInviteStatus(InviteStatus.CANCEL);
      invite.setCanceledDateTime(LocalDateTime.now());
      inviteRepository.save(invite);
    });

    // 경기 삭제
    GameEntity gameEntity = DeleteRequest.toEntity(game);
    gameRepository.save(gameEntity);

    return DeleteGameResponse.toDto(gameEntity);
  }

  /**
   * 경기 팀원 탈퇴
   */
  private WithDrawGameResponse withdrewGame(GameEntity game) {

    ParticipantGameEntity participantGameEntity =
        participantGameRepository.findByStatusAndGameEntityGameIdAndUserEntityUserId
            (ACCEPT, game.getGameId(), user.getUserId())
            .orElseThrow(() -> new CustomException(NOT_PARTICIPANT_FOUND));

    ParticipantGameEntity result =
        ParticipantGameEntity.setWithdraw(participantGameEntity);

    participantGameRepository.save(result);

    return WithDrawGameResponse.toDto(result);
  }

}
