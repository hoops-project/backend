package com.zerobase.hoops.gameCreator.service;

import static com.zerobase.hoops.exception.ErrorCode.USER_NOT_FOUND;
import static com.zerobase.hoops.gameCreator.type.ParticipantGameStatus.ACCEPT;

import com.zerobase.hoops.entity.ParticipantGameEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.gameCreator.dto.ParticipantDto.DetailResponse;
import com.zerobase.hoops.gameCreator.repository.ParticipantGameRepository;
import com.zerobase.hoops.security.TokenProvider;
import com.zerobase.hoops.users.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ParticipantGameService {

  private final ParticipantGameRepository participantGameRepository;

  private final UserRepository userRepository;

  private final TokenProvider tokenProvider;

  /**
   * 경기 참가자 리스트 조회
   */
  public List<DetailResponse> getParticipantList(Long gameId) {

    log.info("getParticipantList start");

    List<ParticipantGameEntity> list = participantGameRepository
        .findByStatusAndGameEntityGameId(ACCEPT, gameId);

    List<DetailResponse> detailResponseList = list.stream()
        .map(DetailResponse::toDto)
        .toList();

    log.info("getParticipantList end");

    return detailResponseList;
  }
}
