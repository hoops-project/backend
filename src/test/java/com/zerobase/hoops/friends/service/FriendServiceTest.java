package com.zerobase.hoops.friends.service;

import static com.zerobase.hoops.friends.type.FriendStatus.ACCEPT;
import static com.zerobase.hoops.friends.type.FriendStatus.APPLY;
import static com.zerobase.hoops.friends.type.FriendStatus.CANCEL;
import static com.zerobase.hoops.friends.type.FriendStatus.DELETE;
import static com.zerobase.hoops.friends.type.FriendStatus.REJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.alarm.repository.EmitterRepository;
import com.zerobase.hoops.alarm.repository.NotificationRepository;
import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyRequest;
import com.zerobase.hoops.friends.dto.FriendDto.CommonRequest;
import com.zerobase.hoops.friends.dto.FriendDto.InviteFriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.FriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RequestFriendListResponse;
import com.zerobase.hoops.friends.repository.FriendRepository;
import com.zerobase.hoops.friends.repository.impl.FriendCustomRepositoryImpl;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

  @InjectMocks
  private FriendService friendService;

  @Mock
  private NotificationService notificationService;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  @Mock
  private UserRepository userRepository;

  @Mock
  private FriendRepository friendRepository;

  @Mock
  private FriendCustomRepositoryImpl friendCustomRepository;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private EmitterRepository emitterRepository;

  @Spy
  private Clock clock;

  private UserEntity user;
  private UserEntity friendUser1;
  private UserEntity friendUser2;
  private FriendEntity expectedApplyFriendEntity;
  private LocalDateTime fixedCreateDateTime;
  private LocalDateTime fixedCancelDateTime;
  private LocalDateTime fixedAcceptDateTime;
  private LocalDateTime fixedRejectDateTime;
  private LocalDateTime fixedDeleteDateTime;

  @BeforeEach
  void setUp() throws Exception{
    fixedCreateDateTime = LocalDateTime
        .of(2024, 6, 9, 0, 0, 0);
    fixedCancelDateTime = LocalDateTime
        .of(2024, 6, 10, 0, 0, 0);
    fixedAcceptDateTime = LocalDateTime
        .of(2024, 6, 10, 0, 0, 0);
    fixedRejectDateTime = LocalDateTime
        .of(2024, 6, 10, 0, 0, 0);
    fixedDeleteDateTime = LocalDateTime
        .of(2024, 6, 10, 0, 0, 0);
    user = UserEntity.builder()
        .id(1L)
        .loginId("test")
        .password("Testpass12!@")
        .email("test@example.com")
        .name("test")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("test")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(new ArrayList<>(List.of("ROLE_USER")))
        .createdDateTime(fixedCreateDateTime)
        .emailAuth(true)
        .build();
    friendUser1 = UserEntity.builder()
        .id(2L)
        .loginId("test1")
        .password("Testpass12!@")
        .email("test1@example.com")
        .name("test1")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("test1")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(new ArrayList<>(List.of("ROLE_USER")))
        .createdDateTime(fixedCreateDateTime)
        .emailAuth(true)
        .build();
    friendUser2 = UserEntity.builder()
        .id(3L)
        .loginId("test2")
        .password("Testpass12!@")
        .email("test2@example.com")
        .name("test2")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("test2")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .roles(new ArrayList<>(List.of("ROLE_USER")))
        .createdDateTime(fixedCreateDateTime)
        .emailAuth(true)
        .build();
    expectedApplyFriendEntity = FriendEntity.builder()
        .id(1L)
        .status(APPLY)
        .createdDateTime(fixedCreateDateTime)
        .user(user)
        .friendUser(friendUser1)
        .build();
  }

  @Test
  @DisplayName("친구 신청 성공")
  void testApplyFriendSuccess() {
    // Given
    ApplyRequest request = ApplyRequest.builder()
        .friendUserId(2L)
        .build();

    FriendEntity applyFriendEntity = ApplyRequest.toEntity(user, friendUser1);

    getCurrentUser(user);

    // ArgumentCaptor를 사용하여 저장된 엔티티를 캡처합니다.
    ArgumentCaptor<FriendEntity> friendEntityArgumentCaptor = ArgumentCaptor.forClass(FriendEntity.class);

    // 친구 신청, 수락 상태가 없다고 가정
    existsApplyOrAcceptFriend(user.getId(), request.getFriendUserId(), false);
    
    // 자신 친구가 10명이 있다고 가정
    countsFriend(user.getId(), 10);

    // 상대방 친구가 10명이 있다고 가정
    countsFriend(request.getFriendUserId(), 10);

    // 친구 유저 조회
    getFriendUser(request.getFriendUserId(), friendUser1);

    when(friendRepository.save(applyFriendEntity)).thenAnswer(invocation -> {
      FriendEntity savedFriendEntity = invocation.getArgument(0);
      savedFriendEntity.setId(1L); // friendId 동적 할당
      savedFriendEntity.setCreatedDateTime(fixedCreateDateTime);
      return savedFriendEntity;
    });

    // when
    friendService.validApplyFriend(request);

    // Then
    verify(friendRepository).save(friendEntityArgumentCaptor.capture());

    // 저장된 엔티티 캡처
    FriendEntity savedFriendEntity = friendEntityArgumentCaptor.getValue();

    assertEquals(expectedApplyFriendEntity, savedFriendEntity);
  }

  @Test
  @DisplayName("친구 신청 실패 : 자기 자신을 친구 신청 할때")
  void testApplyFriendFailIfSelfFriendApply() {
    // Given
    ApplyRequest request = ApplyRequest.builder()
        .friendUserId(1L)
        .build();

    getCurrentUser(user);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validApplyFriend(request);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_FRIEND, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 실패 : 이미 친구 신청, 수락 상태 일때")
  void testApplyFriendFailIfAlreadyApplyOrAccept() {
    // Given
    ApplyRequest request = ApplyRequest.builder()
        .friendUserId(2L)
        .build();

    getCurrentUser(user);

    // 친구 신청, 수락 상태가 있다고 가정
    existsApplyOrAcceptFriend(user.getId(), request.getFriendUserId(), true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validApplyFriend(request);
    });

    // then
    assertEquals(ErrorCode.ALREADY_APPLY_ACCEPT_STATUS, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 실패 : 자신 친구가 30명 일때")
  void testApplyFriendFailIfMyFriendFull() {
    // Given
    ApplyRequest request = ApplyRequest.builder()
        .friendUserId(2L)
        .build();

    getCurrentUser(user);

    // 친구 신청, 수락 상태가 없다고 가정
    existsApplyOrAcceptFriend(user.getId(), request.getFriendUserId(), false);

    // 자신 친구가 30명이 있다고 가정
    countsFriend(user.getId(), 30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validApplyFriend(request);
    });

    // then
    assertEquals(ErrorCode.SELF_FRIEND_FULL, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 실패 : 상대방 친구가 30명 일때")
  void testApplyFriendFailIfOtherFriendFull() {
    // Given
    ApplyRequest request = ApplyRequest.builder()
        .friendUserId(2L)
        .build();

    getCurrentUser(user);

    // 친구 신청, 수락 상태가 없다고 가정
    existsApplyOrAcceptFriend(user.getId(), request.getFriendUserId(), false);

    // 자신 친구가 30명이 있다고 가정
    countsFriend(user.getId(), 10);

    countsFriend(request.getFriendUserId(), 30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validApplyFriend(request);
    });

    // then
    assertEquals(ErrorCode.OTHER_FRIEND_FULL, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 취소 성공")
  void testCancelFriendSuccess() {
    // Given
    CommonRequest request = CommonRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity expectedCancelFriendEntity = FriendEntity.builder()
        .id(expectedApplyFriendEntity.getId())
        .status(CANCEL)
        .createdDateTime(fixedCreateDateTime)
        .canceledDateTime(fixedCancelDateTime)
        .user(user)
        .friendUser(friendUser1)
        .build();

    Instant fixedInstant = fixedCancelDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    FriendEntity cancelEntity = FriendEntity.setCancel(
        expectedApplyFriendEntity, clock);

    getCurrentUser(user);

    // 친구 신청 entity 조회
    getFriendEntity(request.getFriendId());

    when(friendRepository.save(cancelEntity)).thenReturn(cancelEntity);

    // when
    friendService.validCancelFriend(request);

    // Then
    assertEquals(expectedCancelFriendEntity, cancelEntity);
  }

  @Test
  @DisplayName("친구 신청 취소 실패 : 자기 자신이 한 친구 신청이 아님")
  void testCancelFriendFailIfNotSelfFriendApply() {
    // Given
    CommonRequest request = CommonRequest.builder()
        .friendId(1L)
        .build();

    expectedApplyFriendEntity = FriendEntity.builder()
        .id(1L)
        .user(friendUser1)
        .build();

    getCurrentUser(user);

    // 친구 신청 entity 조회
    getFriendEntity(request.getFriendId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validCancelFriend(request);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_APPLY, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 수락 성공")
  void testAcceptFriendSuccess() {
    // Given
    CommonRequest request = CommonRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity expectedMyAcceptFriendEntity = FriendEntity.builder()
        .id(expectedApplyFriendEntity.getId())
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(user)
        .friendUser(friendUser1)
        .build();

    FriendEntity expectedOtherAcceptFriendEntity = FriendEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(friendUser1)
        .friendUser(user)
        .build();

    Instant fixedInstant = fixedAcceptDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    FriendEntity myFriendEntity = FriendEntity.setAcceptMyFriend(
        expectedApplyFriendEntity, clock);

    FriendEntity otherFriendEntity
        = FriendEntity.setAcceptOtherFriend(myFriendEntity);

    // ArgumentCaptor를 사용하여 저장된 엔티티를 캡처합니다.
    ArgumentCaptor<FriendEntity> friendEntityArgumentCaptor
        = ArgumentCaptor.forClass(FriendEntity.class);

    getCurrentUser(friendUser1);

    // 친구 신청 entity 조회
    getFriendEntity(request.getFriendId());

    // 자신의 친구가 10명이 있다고 가정
    countsFriend(expectedApplyFriendEntity.getFriendUser().getId(),10);

    // 상대방의 친구가 10명이 있다고 가정
    countsFriend(expectedApplyFriendEntity.getUser().getId(),10);

    when(friendRepository.save(myFriendEntity)).thenReturn(myFriendEntity);

    when(friendRepository.save(otherFriendEntity)).thenAnswer(invocation -> {
      FriendEntity savedFriendEntity = invocation.getArgument(0);
      savedFriendEntity.setId(2L); // friendId 동적 할당
      savedFriendEntity.setAcceptedDateTime(fixedAcceptDateTime);
      return savedFriendEntity;
    });

    // when
    friendService.validAcceptFriend(request);

    // Then
    verify(friendRepository, times(2))
        .save(friendEntityArgumentCaptor.capture());

    // 저장된 엔티티 캡처
    FriendEntity savedFriendEntity =
        friendEntityArgumentCaptor.getAllValues().get(1);

    assertEquals(expectedMyAcceptFriendEntity, myFriendEntity);
    assertEquals(expectedOtherAcceptFriendEntity, savedFriendEntity);
  }

  @Test
  @DisplayName("친구 수락 실패 : 자신이 받은 친구 신청이 아님")
  void testAcceptFriendFailIfNotSelfReceiveFriendApply() {
    // Given
    CommonRequest request = CommonRequest.builder()
        .friendId(1L)
        .build();

    expectedApplyFriendEntity = FriendEntity.builder()
        .id(1L)
        .friendUser(user)
        .build();

    getCurrentUser(friendUser1);

    // 친구 신청 entity 조회
    getFriendEntity(request.getFriendId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validAcceptFriend(request);
    });

    // then
    assertEquals(ErrorCode.NOT_MY_RECEIVE, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 수락 실패 : 자신 친구 목록이 30명 일때")
  void testAcceptFriendFailIfMyFriendFull() {
    // Given
    CommonRequest request = CommonRequest.builder()
        .friendId(1L)
        .build();

    getCurrentUser(friendUser1);

    // 친구 신청 entity 조회
    getFriendEntity(request.getFriendId());

    // 자신의 친구가 10명이 있다고 가정
    countsFriend(expectedApplyFriendEntity.getFriendUser().getId(),30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validAcceptFriend(request);
    });

    // then
    assertEquals(ErrorCode.SELF_FRIEND_FULL, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 수락 실패 : 상대방 친구 목록이 30명 일때")
  void testAcceptFriendFailIfOtherFriendFull() {
    // Given
    CommonRequest request = CommonRequest.builder()
        .friendId(1L)
        .build();

    getCurrentUser(friendUser1);

    // 친구 신청 entity 조회
    getFriendEntity(request.getFriendId());

    // 자신의 친구가 10명이 있다고 가정
    countsFriend(expectedApplyFriendEntity.getFriendUser().getId(),10);

    // 상대방의 친구가 10명이 있다고 가정
    countsFriend(expectedApplyFriendEntity.getUser().getId(),30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validAcceptFriend(request);
    });

    // then
    assertEquals(ErrorCode.OTHER_FRIEND_FULL, exception.getErrorCode());
  }


  @Test
  @DisplayName("친구 거절 성공")
  void testRejectFriendSuccess() {
    // Given
    CommonRequest request = CommonRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity expectedRejectFriendEntity = FriendEntity.builder()
        .id(expectedApplyFriendEntity.getId())
        .status(REJECT)
        .createdDateTime(fixedCreateDateTime)
        .rejectedDateTime(fixedRejectDateTime)
        .user(user)
        .friendUser(friendUser1)
        .build();

    Instant fixedInstant = fixedRejectDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    FriendEntity rejectFriendEntity = FriendEntity.setReject(
        expectedApplyFriendEntity, clock);

    getCurrentUser(friendUser1);

    // 친구 신청 entity 조회
    getFriendEntity(request.getFriendId());

    when(friendRepository.save(rejectFriendEntity)).thenReturn(rejectFriendEntity);

    // when
    friendService.validRejectFriend(request);

    // Then
    assertEquals(expectedRejectFriendEntity, rejectFriendEntity);
  }

  @Test
  @DisplayName("친구 거절 실패 : 자신이 받은 친구 신청이 아님")
  void testRejectFriendFailIfNotSelfReceiveFriendApply() {
    // Given
    CommonRequest request = CommonRequest.builder()
        .friendId(1L)
        .build();

    expectedApplyFriendEntity = FriendEntity.builder()
        .id(1L)
        .friendUser(user)
        .build();

    getCurrentUser(friendUser1);

    when(friendRepository.findByIdAndStatus(request.getFriendId(), APPLY))
        .thenReturn(Optional.ofNullable(expectedApplyFriendEntity));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validRejectFriend(request);
    });

    // then
    assertEquals(ErrorCode.NOT_MY_RECEIVE, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 삭제 성공")
  void testDeleteFriendSuccess() {
    // Given
    CommonRequest request = CommonRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity myFriendAcceptEntity = FriendEntity.builder()
        .id(1L)
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(user)
        .friendUser(friendUser1)
        .build();

    FriendEntity otherFriendAcceptEntity = FriendEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(friendUser1)
        .friendUser(user)
        .build();

    FriendEntity expectedMyDeleteFriendEntity = FriendEntity.builder()
        .id(1L)
        .status(DELETE)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .deletedDateTime(fixedDeleteDateTime)
        .user(user)
        .friendUser(friendUser1)
        .build();

    FriendEntity expectedOtherDeleteFriendEntity = FriendEntity.builder()
        .id(2L)
        .status(DELETE)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .deletedDateTime(fixedDeleteDateTime)
        .user(friendUser1)
        .friendUser(user)
        .build();

    Instant fixedInstant = fixedRejectDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    FriendEntity myDeleteFriendEntity =
        FriendEntity.setDeleteMyFriend(myFriendAcceptEntity, clock);

    FriendEntity otherDeleteFriendEntity =
        FriendEntity.setDeleteOtherFriend(myDeleteFriendEntity, otherFriendAcceptEntity);

    getCurrentUser(user);

    when(friendRepository.findByIdAndStatus
        (request.getFriendId(), ACCEPT))
        .thenReturn(Optional.of(myFriendAcceptEntity));

    when(friendRepository.findByFriendUserIdAndUserIdAndStatus(
        user.getId(), myFriendAcceptEntity.getFriendUser().getId(), ACCEPT))
        .thenReturn(Optional.of(otherFriendAcceptEntity));

    when(friendRepository.save(myDeleteFriendEntity)).thenReturn(myDeleteFriendEntity);
    when(friendRepository.save(otherDeleteFriendEntity)).thenReturn(otherDeleteFriendEntity);

    // when
    friendService.validDeleteFriend(request);

    // Then
    assertEquals(expectedMyDeleteFriendEntity, myDeleteFriendEntity);
    assertEquals(expectedOtherDeleteFriendEntity, otherDeleteFriendEntity);
  }

  @Test
  @DisplayName("친구 삭제 실패 : 내가 받은 친구가 아닐때")
  void testDeleteFriendFailIfNotMyReceiveFriendAccept() {
    // Given
    CommonRequest request = CommonRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity selfEntity = FriendEntity.builder()
        .id(1L)
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(friendUser1)
        .friendUser(friendUser1)
        .build();

    getCurrentUser(user);

    when(friendRepository.findByIdAndStatus(request.getFriendId(), ACCEPT))
        .thenReturn(Optional.ofNullable(selfEntity));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validDeleteFriend(request);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_ACCEPT, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 검색 성공")
  void testSearchNickNameSuccess() {
    // Given
    String nickName = "test";
    Pageable pageable = PageRequest.of(0, 4);

    FriendListResponse friendListResponse1 = FriendListResponse.builder()
        .userId(2L)
        .birthday(LocalDate.of(1990, 1, 1))
        .nickName("test1")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .friendId(1L)
        .build();

    FriendListResponse friendListResponse2 = FriendListResponse.builder()
        .userId(3L)
        .birthday(LocalDate.of(1990, 1, 1))
        .nickName("test2")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .friendId(null)
        .build();

    List<FriendListResponse> listResponseFriendList =
        List.of(friendListResponse1, friendListResponse2);

    Page<FriendListResponse> searchResponsePage =
        new PageImpl<>(listResponseFriendList, pageable, 2);

    getCurrentUser(user);

    when(friendCustomRepository.findBySearchFriendList
        (user.getId(), nickName, pageable)).thenReturn(searchResponsePage);

    // when
    Page<FriendListResponse> result =
        friendService.validSearchNickName(nickName, pageable);

    // Then
    assertEquals(searchResponsePage, result);
  }

  @Test
  @DisplayName("친구 검색 실패 : 검색할 닉네임을 입력 안했을때")
  void testSearchNickNameFailIfNickNameIsBlank() {
    // Given
    String nickName = "";
    Pageable pageable = PageRequest.of(0, 10);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validSearchNickName(nickName, pageable);
    });

    // then
    assertEquals(ErrorCode.NOT_FOUND_NICKNAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 리스트 조회 성공")
  void testGetMyFriendsSuccess() {
    // Given
    Pageable pageable = PageRequest.of(0, 1);

    FriendEntity friendEntity1 = FriendEntity.builder()
        .id(1L)
        .status(ACCEPT)
        .user(user)
        .friendUser(friendUser1)
        .build();

    List<FriendEntity> friendEntityList =
        List.of(friendEntity1);

    Page<FriendEntity> searchResponsePage =
        new PageImpl<>(friendEntityList, pageable, 1);

    List<FriendListResponse> listResponseFriendList = searchResponsePage.stream()
        .map(FriendListResponse::toDto)
        .toList();

    getCurrentUser(user);

    when(friendRepository.findByStatusAndUserId
        (ACCEPT, user.getId(), pageable))
        .thenReturn(searchResponsePage);

    // when
    List<FriendListResponse> result = friendService.validGetMyFriends(pageable);

    // Then
    assertEquals(listResponseFriendList, result);
  }

  @Test
  @DisplayName("경기 초대 친구 리스트 조회")
  void testGetMyInviteListSuccess() {
    // Given
    Long gameId = 1L;
    Pageable pageable = PageRequest.of(0, 1);

    InviteFriendListResponse inviteFriendListResponse = InviteFriendListResponse.builder()
        .userId(2L)
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("test1")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .mannerPoint("0")
        .status(InviteStatus.REQUEST)
        .build();

    List<InviteFriendListResponse> inviteListResponseFriendList =
        List.of(inviteFriendListResponse);

    Page<InviteFriendListResponse> inviteListResponsePage =
        new PageImpl<>(inviteListResponseFriendList, pageable, 1);

    getCurrentUser(user);

    when(friendCustomRepository.findByMyInviteFriendList
        (user.getId(), gameId, pageable))
        .thenReturn(inviteListResponsePage);

    // when
    Page<InviteFriendListResponse> result = friendService.validGetMyInviteList(gameId, pageable);

    // Then
    assertEquals(inviteListResponsePage, result);
  }

  @Test
  @DisplayName("내가 친구 요청 받은 리스트 조회 성공")
  void testGetRequestFriendListSuccess() {
    // Given
    Pageable pageable = PageRequest.of(0, 1);

    FriendEntity friendEntity1 = FriendEntity.builder()
        .id(1L)
        .status(ACCEPT)
        .user(friendUser1)
        .friendUser(user)
        .build();

    FriendEntity friendEntity2 = FriendEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .user(friendUser2)
        .friendUser(user)
        .build();

    List<FriendEntity> friendEntityList =
        List.of(friendEntity1, friendEntity2);

    Page<FriendEntity> friendEntityPage =
        new PageImpl<>(friendEntityList, pageable, 1);

    List<RequestFriendListResponse> expectedRequestFriendList
        = friendEntityList.stream()
        .map(RequestFriendListResponse::toDto)
        .toList();

    getCurrentUser(user);

    when(friendRepository.findByStatusAndFriendUserId
        (FriendStatus.APPLY, user.getId(), pageable))
        .thenReturn(friendEntityPage);

    // when
    List<RequestFriendListResponse> requestFriendList
        = friendService.validGetRequestFriendList(pageable);

    // Then
    assertEquals(expectedRequestFriendList, requestFriendList);
  }
  
  // 로그인 유저 정보 조회
  private void getCurrentUser(UserEntity userEntity) {
    when(jwtTokenExtract.currentUser()).thenReturn(userEntity);

    when(userRepository.findById(userEntity.getId())).thenReturn(
        Optional.of(userEntity));
  }

  // 친구 신청, 수락 상태 검사
  private void existsApplyOrAcceptFriend(Long userId, Long friendUserId,
      boolean flag) {
    when(friendRepository.existsByUserIdAndFriendUserIdAndStatusIn(
        userId, friendUserId, List.of(APPLY, ACCEPT))).thenReturn(flag);
  }

  // 친구 몇명인지 계산
  private void countsFriend(Long userId, int count) {
    when(friendRepository.countByUserIdAndStatus(userId, ACCEPT))
        .thenReturn(count);
  }

  // 친구 조회
  private void getFriendUser(Long userId, UserEntity user) {
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
  }

  // 친구 신청 entity 조회
  private void getFriendEntity(Long friendId) {
    when(friendRepository.findByIdAndStatus(friendId, APPLY))
        .thenReturn(Optional.of(expectedApplyFriendEntity));
  }

}