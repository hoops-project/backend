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

import com.zerobase.hoops.chat.repository.ChatRoomRepository;
import com.zerobase.hoops.entity.ChatRoomEntity;
import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.entity.InviteEntity;
import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.gameCreator.dto.GameDto.CreateRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.DeleteRequest;
import com.zerobase.hoops.gameCreator.dto.GameDto.DetailResponse;
import com.zerobase.hoops.gameCreator.dto.GameDto.ParticipantUser;
import com.zerobase.hoops.gameCreator.dto.GameDto.UpdateRequest;
import com.zerobase.hoops.gameCreator.repository.GameRepository;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import com.zerobase.hoops.invite.repository.InviteRepository;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.GenderType;
import java.time.Clock;
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

  private final ChatRoomRepository chatRoomRepository;

  private final Clock clock;

  private static UserEntity user;

  /**
   * 경기 생성 전 validation 체크
   */
  public String validCreateGame(CreateRequest request) {
    log.info("createGame start");

    setUpUser();

    validCreateAndUpdateGame(request.getHeadCount(),
        request.getStartDateTime(), request.getAddress(),
        request.getMatchFormat(), null);

    createGame(request);

    log.info("createGame end");
    return request.getTitle() + " 경기가 생성되었습니다.";
  }

  /**
   * 경기 생성
   */
  private void createGame(CreateRequest request) {
    // 경기 생성
    GameEntity game = CreateRequest.toEntity(request, user);

    gameRepository.save(game);

    // 경기 개설자는 경기에 참가인 상태로 있어야 함
    ParticipantGameEntity participantGame =
        ParticipantGameEntity.toGameCreatorEntity(game, user, clock);

    participantGameRepository.save(participantGame);

    // 채팅 룸 개설
    ChatRoomEntity chatRoom = new ChatRoomEntity();
    chatRoom.saveGameInfo(game);
    chatRoomRepository.save(chatRoom);
  }

  /**
   * 경기 상세 조회
   */
  public DetailResponse getGameDetail(Long gameId) {
    GameEntity game = gameRepository.findByIdAndDeletedDateTimeNull(gameId)
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    List<ParticipantGameEntity> participantGameEntityList =
        participantGameRepository
            .findByGameIdAndStatusAndDeletedDateTimeNull
                (game.getId(), ACCEPT);

    List<ParticipantUser> participantUserList =
        participantGameEntityList.stream().map(ParticipantUser::toDto).toList();

    return DetailResponse.toDto(game, participantUserList);
  }

  /**
   * 경기 수정 전 validation 체크
   */
  public String validUpdateGame(UpdateRequest request) {
    log.info("updateGame start");

    setUpUser();

    // 게임 아이디로 게임 조회, 먼저 삭제 되었는지 조회
    GameEntity game =
        gameRepository.findByIdAndDeletedDateTimeNull(request.getGameId())
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    //자신이 경기 개최자가 아니면 수정 못하게
    if(!Objects.equals(user.getId(), game.getUser().getId())) {
      throw new CustomException(NOT_GAME_CREATOR);
    }

    validCreateAndUpdateGame(request.getHeadCount(),
        request.getStartDateTime(), request.getAddress(),
        request.getMatchFormat(), game.getId());

    /**
     * 예) 변경 하려는 인원수 : 6
     *     현재 경기에 수락된 인원수 : 8
     *     이 경우 Exception 발생
     */
    int headCount =
        participantGameRepository.countByStatusAndGameId
            (ACCEPT, game.getId());

    if (request.getHeadCount() < headCount) {
      throw new CustomException(NOT_UPDATE_HEADCOUNT);
    }

    // 수정하려는 성별이 ALL 이면 이 메서드 통과
    Gender gender = request.getGender();
    if (gender == MALEONLY || gender == FEMALEONLY) {
      GenderType queryGender = gender == MALEONLY ? FEMALE : MALE;

      boolean genderExist = participantGameRepository
          .existsByStatusAndGameIdAndUserGender
              (ACCEPT, game.getId(), queryGender);

      /**
       * 예) 수정하려는 성별 : MALEONLY
       *     경기에 수락된 인원들중 FEMALE 갯수를 검사
       *     FEMALE이 한명이라도 있으면 안되므로 Exception 발생
       */
      if (genderExist) {
        if (gender == MALEONLY) {
          throw new CustomException(NOT_UPDATE_MAN);
        } else {
          throw new CustomException(NOT_UPDATE_WOMAN);
        }
      }
    }

    updateGame(request, game);

    log.info("updateGame end");
    return request.getTitle() + " 경기가 수정되었습니다.";
  }

  /**
   * 경기 수정
   */
  private void updateGame(UpdateRequest request, GameEntity game) {
    GameEntity gameEntity = UpdateRequest.toEntity(request, game);
    gameRepository.save(gameEntity);
  }

  /**
   * 경기 삭제 전 validation 체크
   */
  public String validDeleteGame(DeleteRequest request) {
    setUpUser();

    // 경기 아이디로 게임 조회, 먼저 삭제 되었는지 조회
    GameEntity game = gameRepository.findByIdAndDeletedDateTimeNull(request.getGameId())
        .orElseThrow(() -> new CustomException(GAME_NOT_FOUND));

    // 설정한 경기 시작 30분 전에만 삭제 가능
    LocalDateTime beforeDatetime = game.getStartDateTime().minusMinutes(30);
    LocalDateTime nowDateTime = LocalDateTime.now();

    if(nowDateTime.isAfter(beforeDatetime)) {
      throw new CustomException(NOT_DELETE_STARTDATE);
    }

    // 경기 개최자가 삭제 시 -> 경기 삭제
    if(Objects.equals(user.getId(), game.getUser().getId())) {
      return deleteGame(game);
    } else { // 팀원이 삭제 시 -> 팀원 탈퇴
      return withdrewGame(game);
    }

  }

  /**
   * 경기 삭제
   */
  private String deleteGame(GameEntity game) {

    // 경기 삭제 전에 기존에 경기에 ACCEPT, APPLY 멤버들 조회
    List<ParticipantGameEntity> participantGameEntityList =
        participantGameRepository.findByStatusInAndGameId
            (List.of(ACCEPT, APPLY), game.getId());

    participantGameEntityList.forEach(participantGame -> {
      ParticipantGameEntity entity =
          ParticipantGameEntity.setDelete(participantGame, clock);
      participantGameRepository.save(entity);
    });

    // 해당 경기에 초대 신청된 것들 다 조회
    List<InviteEntity> inviteEntityList = inviteRepository
        .findByInviteStatusAndGameId
            (InviteStatus.REQUEST, game.getId());

    inviteEntityList.forEach(invite -> {
      InviteEntity entity = InviteEntity.setCancel(invite, clock);
      inviteRepository.save(entity);
    });

    // 경기 삭제
    GameEntity gameEntity = DeleteRequest.toEntity(game, clock);
    gameRepository.save(gameEntity);

    return game.getTitle() + " 경기가 삭제되었습니다.";
  }

  /**
   * 경기 팀원 탈퇴
   */
  private String withdrewGame(GameEntity game) {

    ParticipantGameEntity participantGameEntity =
        participantGameRepository.findByStatusAndGameIdAndUserId
            (ACCEPT, game.getId(), user.getId())
            .orElseThrow(() -> new CustomException(NOT_PARTICIPANT_FOUND));

    ParticipantGameEntity result =
        ParticipantGameEntity.setWithdraw(participantGameEntity, clock);

    participantGameRepository.save(result);

    return game.getTitle() + " 경기에 탈퇴했습니다.";
  }

  /**
   * 로그인 유저 조회
   */
  private void setUpUser() {
    Long userId = jwtTokenExtract.currentUser().getId();

    user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }

  /**
   * 경기 생성, 수정 공통 validation
   */
  private void validCreateAndUpdateGame(Long headCount,
      LocalDateTime startDateTime, String address,
      MatchFormat matchFormat, Long gameId) {

    LocalDateTime beforeDateTime =
        startDateTime.minusHours(1).plusSeconds(1); // 13:00:01
    LocalDateTime afterDateTime = startDateTime.plusHours(1).minusSeconds(1); // 14:59:59
    LocalDateTime nowDateTime = LocalDateTime.now();

    boolean gameExists = false;

    // 경기 생성
    if(gameId == null) {
      gameExists = gameRepository
          .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNull
              (beforeDateTime, afterDateTime, address);
    } else { // 경기 수정
      gameExists = gameRepository
          .existsByStartDateTimeBetweenAndAddressAndDeletedDateTimeNullAndIdNot
              (beforeDateTime, afterDateTime, address, gameId);
    }

    // 시간 범위에 해당하는 해당 주소에서 예정된 경기를 찾을시
    if(gameExists) {
      throw new CustomException(ALREADY_GAME_CREATED);
    } else { // 예정된 경기를 못찾을시
      // 입력한 경기 시작 시간은 현재시간 30분 보다 후 여야 함
      if(startDateTime.minusMinutes(30).isBefore(nowDateTime)) {
        throw new CustomException(NOT_AFTER_THIRTY_MINUTE);
      }
    }

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
  }

}
