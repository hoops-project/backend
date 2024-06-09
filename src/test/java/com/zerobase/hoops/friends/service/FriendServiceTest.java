package com.zerobase.hoops.friends.service;

import static com.zerobase.hoops.friends.type.FriendStatus.ACCEPT;
import static com.zerobase.hoops.friends.type.FriendStatus.APPLY;
import static com.zerobase.hoops.friends.type.FriendStatus.CANCEL;
import static com.zerobase.hoops.friends.type.FriendStatus.DELETE;
import static com.zerobase.hoops.friends.type.FriendStatus.REJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.zerobase.hoops.friends.dto.FriendDto.AcceptRequest;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyRequest;
import com.zerobase.hoops.friends.dto.FriendDto.CancelRequest;
import com.zerobase.hoops.friends.dto.FriendDto.DeleteRequest;
import com.zerobase.hoops.friends.dto.FriendDto.InviteFriendListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RejectRequest;
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

  private UserEntity userEntity;
  private UserEntity friendUserEntity1;
  private UserEntity friendUserEntity2;
  private FriendEntity applyFriendEntity;
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
    userEntity = UserEntity.builder()
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
        .createdDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
    friendUserEntity1 = UserEntity.builder()
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
        .createdDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
    friendUserEntity2 = UserEntity.builder()
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
        .createdDateTime(LocalDateTime.now())
        .emailAuth(true)
        .build();
    applyFriendEntity = FriendEntity.builder()
        .id(1L)
        .status(APPLY)
        .createdDateTime(fixedCreateDateTime)
        .user(userEntity)
        .friendUser(friendUserEntity1)
        .build();
  }

  @Test
  @DisplayName("친구 신청 성공")
  void applyFriend_success() {
    // Given
    ApplyRequest request = ApplyRequest.builder()
        .friendUserId(2L)
        .build();

    FriendEntity applyFriendEntity = ApplyRequest.toEntity(userEntity,
        friendUserEntity1);

    getCurrentUser(userEntity);

    // ArgumentCaptor를 사용하여 저장된 엔티티를 캡처합니다.
    ArgumentCaptor<FriendEntity> friendEntityArgumentCaptor = ArgumentCaptor.forClass(FriendEntity.class);

    // 친구 신청, 수락 상태가 없다고 가정
    when(friendRepository.existsByUserIdAndFriendUserIdAndStatusIn(
        userEntity.getId(), request.getFriendUserId(), List.of(APPLY, ACCEPT)))
        .thenReturn(false);

    // 자신 및 상대방 친구목록에 10명이 있다고 가정
    when(friendRepository.countByUserIdAndStatus(userEntity.getId(), ACCEPT))
        .thenReturn(10);

    when(friendRepository.countByUserIdAndStatus(request.getFriendUserId(), ACCEPT))
        .thenReturn(10);

    when(userRepository.findById(request.getFriendUserId()))
        .thenReturn(Optional.of(friendUserEntity1));

    when(friendRepository.save(applyFriendEntity)).thenAnswer(invocation -> {
      FriendEntity savedFriendEntity = invocation.getArgument(0);
      savedFriendEntity.setId(1L); // friendId 동적 할당
      savedFriendEntity.setCreatedDateTime(fixedCreateDateTime);
      return savedFriendEntity;
    });

    // when
    friendService.applyFriend(request);

    // Then
    verify(friendRepository).save(friendEntityArgumentCaptor.capture());

    // 저장된 엔티티 캡처
    FriendEntity savedFriendEntity = friendEntityArgumentCaptor.getValue();

    assertEquals(this.applyFriendEntity, savedFriendEntity);
  }

  @Test
  @DisplayName("친구 신청 실패 : 자기 자신을 친구 신청 할때")
  void applyFriend_failIfSelfFriendApply() {
    // Given
    ApplyRequest request = ApplyRequest.builder()
        .friendUserId(1L)
        .build();

    getCurrentUser(userEntity);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.applyFriend(request);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_FRIEND, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 실패 : 이미 친구 신청, 수락 상태 일때")
  void applyFriend_failIfAlreadyApplyOrAccept() {
    // Given
    ApplyRequest request = ApplyRequest.builder()
        .friendUserId(2L)
        .build();

    getCurrentUser(userEntity);

    // 친구 신청, 수락 상태가 없다고 가정
    when(friendRepository.existsByUserIdAndFriendUserIdAndStatusIn(
        userEntity.getId(), request.getFriendUserId(), List.of(APPLY, ACCEPT)))
        .thenReturn(true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.applyFriend(request);
    });

    // then
    assertEquals(ErrorCode.ALREADY_APPLY_ACCEPT_STATUS, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 실패 : 자신 친구 목록이 30명 일때")
  void applyFriend_failIfMyFriendFull() {
    // Given
    ApplyRequest request = ApplyRequest.builder()
        .friendUserId(2L)
        .build();

    getCurrentUser(userEntity);

    // 친구 신청, 수락 상태가 없다고 가정
    when(friendRepository.existsByUserIdAndFriendUserIdAndStatusIn(
        userEntity.getId(), request.getFriendUserId(), List.of(APPLY, ACCEPT)))
        .thenReturn(false);

    when(friendRepository.countByUserIdAndStatus(userEntity.getId(), ACCEPT))
        .thenReturn(30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.applyFriend(request);
    });

    // then
    assertEquals(ErrorCode.SELF_FRIEND_FULL, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 실패 : 상대방 친구 목록이 30명 일때")
  void applyFriend_failIfOtherFriendFull() {
    // Given
    ApplyRequest request = ApplyRequest.builder()
        .friendUserId(2L)
        .build();

    getCurrentUser(userEntity);

    // 친구 신청, 수락 상태가 없다고 가정
    when(friendRepository.existsByUserIdAndFriendUserIdAndStatusIn(
        userEntity.getId(), request.getFriendUserId(), List.of(APPLY, ACCEPT)))
        .thenReturn(false);

    when(friendRepository.countByUserIdAndStatus(userEntity.getId(), ACCEPT))
        .thenReturn(10);

    when(friendRepository.countByUserIdAndStatus(request.getFriendUserId(), ACCEPT))
        .thenReturn(30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.applyFriend(request);
    });

    // then
    assertEquals(ErrorCode.OTHER_FRIEND_FULL, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 취소 성공")
  void cancelFriend_success() {
    // Given
    CancelRequest request = CancelRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity expectedCancelFriendEntity = FriendEntity.builder()
        .id(applyFriendEntity.getId())
        .status(CANCEL)
        .createdDateTime(fixedCreateDateTime)
        .canceledDateTime(fixedCancelDateTime)
        .user(userEntity)
        .friendUser(friendUserEntity1)
        .build();


    Instant fixedInstant = fixedCancelDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    FriendEntity cancelEntity = CancelRequest.toEntity(applyFriendEntity, clock);

    getCurrentUser(userEntity);

    when(friendRepository.findByIdAndStatus(request.getFriendId(), APPLY))
        .thenReturn(Optional.of(applyFriendEntity));

    when(friendRepository.save(cancelEntity)).thenReturn(cancelEntity);

    // when
    friendService.cancelFriend(request);

    // Then
    assertEquals(expectedCancelFriendEntity, cancelEntity);
  }

  @Test
  @DisplayName("친구 신청 취소 실패 : 자기 자신이 한 친구 신청이 아님")
  void cancelFriend_failIfNotSelfFriendApply() {
    // Given
    CancelRequest request = CancelRequest.builder()
        .friendId(1L)
        .build();

    applyFriendEntity.setUser(friendUserEntity1);

    getCurrentUser(userEntity);

    when(friendRepository.findByIdAndStatus(request.getFriendId(), APPLY))
        .thenReturn(Optional.of(applyFriendEntity));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.cancelFriend(request);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_APPLY, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 수락 성공")
  void acceptFriend_success() {
    // Given
    AcceptRequest request = AcceptRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity expectedMyAcceptFriendEntity = FriendEntity.builder()
        .id(applyFriendEntity.getId())
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(userEntity)
        .friendUser(friendUserEntity1)
        .build();

    FriendEntity expectedOtherAcceptFriendEntity = FriendEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(friendUserEntity1)
        .friendUser(userEntity)
        .build();

    Instant fixedInstant = fixedAcceptDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    FriendEntity myFriendEntity = AcceptRequest.toMyFriendEntity(
        applyFriendEntity, clock);

    FriendEntity otherFriendEntity = AcceptRequest.toOtherFriendEntity(myFriendEntity);

    // ArgumentCaptor를 사용하여 저장된 엔티티를 캡처합니다.
    ArgumentCaptor<FriendEntity> friendEntityArgumentCaptor = ArgumentCaptor.forClass(FriendEntity.class);

    getCurrentUser(friendUserEntity1);

    when(friendRepository.findByIdAndStatus(request.getFriendId(), APPLY))
        .thenReturn(Optional.ofNullable(applyFriendEntity));

    // 자신 또는 상대방의 친구 목록에 10명이 있다고 가정
    when(friendRepository.countByUserIdAndStatus
        (friendUserEntity1.getId(), ACCEPT)).thenReturn(10);

    when(friendRepository.countByUserIdAndStatus(
        applyFriendEntity.getUser().getId(), ACCEPT))
        .thenReturn(10);

    when(friendRepository.save(myFriendEntity)).thenReturn(myFriendEntity);
    when(friendRepository.save(otherFriendEntity)).thenAnswer(invocation -> {
      FriendEntity savedFriendEntity = invocation.getArgument(0);
      savedFriendEntity.setId(2L); // friendId 동적 할당
      savedFriendEntity.setAcceptedDateTime(fixedAcceptDateTime);
      return savedFriendEntity;
    });

    // when
    friendService.acceptFriend(request);

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
  void acceptFriend_failIfNotSelfReceiveFriendApply() {
    // Given
    AcceptRequest request = AcceptRequest.builder()
        .friendId(1L)
        .build();

    applyFriendEntity.setFriendUser(userEntity);

    getCurrentUser(friendUserEntity1);

    when(friendRepository.findByIdAndStatus(request.getFriendId(), APPLY))
        .thenReturn(Optional.ofNullable(applyFriendEntity));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.acceptFriend(request);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_RECEIVE, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 수락 실패 : 자신 친구 목록이 30명 일때")
  void acceptFriend_failIfMyFriendFull() {
    // Given
    AcceptRequest request = AcceptRequest.builder()
        .friendId(1L)
        .build();

    getCurrentUser(friendUserEntity1);

    when(friendRepository.findByIdAndStatus(request.getFriendId(), APPLY))
        .thenReturn(Optional.ofNullable(applyFriendEntity));

    when(friendRepository.countByUserIdAndStatus(friendUserEntity1.getId(), ACCEPT))
        .thenReturn(30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.acceptFriend(request);
    });

    // then
    assertEquals(ErrorCode.SELF_FRIEND_FULL, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 수락 실패 : 상대방 친구 목록이 30명 일때")
  void acceptFriend_failIfOtherFriendFull() {
    // Given
    AcceptRequest request = AcceptRequest.builder()
        .friendId(1L)
        .build();

    getCurrentUser(friendUserEntity1);

    when(friendRepository.findByIdAndStatus(request.getFriendId(), APPLY))
        .thenReturn(Optional.ofNullable(applyFriendEntity));

    when(friendRepository.countByUserIdAndStatus(friendUserEntity1.getId(), ACCEPT))
        .thenReturn(10);

    when(friendRepository.countByUserIdAndStatus(applyFriendEntity.getUser().getId(),
        ACCEPT))
        .thenReturn(30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.acceptFriend(request);
    });

    // then
    assertEquals(ErrorCode.OTHER_FRIEND_FULL, exception.getErrorCode());
  }


  @Test
  @DisplayName("친구 거절 성공")
  void rejectFriend_success() {
    // Given
    RejectRequest request = RejectRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity expectedRejectFriendEntity = FriendEntity.builder()
        .id(applyFriendEntity.getId())
        .status(REJECT)
        .createdDateTime(fixedCreateDateTime)
        .rejectedDateTime(fixedRejectDateTime)
        .user(userEntity)
        .friendUser(friendUserEntity1)
        .build();

    Instant fixedInstant = fixedRejectDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    FriendEntity rejectFriendEntity = RejectRequest.toEntity(applyFriendEntity,
        clock);

    getCurrentUser(friendUserEntity1);

    when(friendRepository.findByIdAndStatus(request.getFriendId(), APPLY))
        .thenReturn(Optional.ofNullable(applyFriendEntity));

    when(friendRepository.save(rejectFriendEntity)).thenReturn(rejectFriendEntity);

    // when
    friendService.rejectFriend(request);

    // Then
    assertEquals(expectedRejectFriendEntity, rejectFriendEntity);
  }

  @Test
  @DisplayName("친구 거절 실패 : 자신이 받은 친구 신청이 아님")
  void rejectFriend_failIfNotSelfReceiveFriendApply() {
    // Given
    RejectRequest request = RejectRequest.builder()
        .friendId(1L)
        .build();

    applyFriendEntity.setFriendUser(userEntity);

    getCurrentUser(friendUserEntity1);

    when(friendRepository.findByIdAndStatus(request.getFriendId(), APPLY))
        .thenReturn(Optional.ofNullable(applyFriendEntity));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.rejectFriend(request);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_RECEIVE, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 삭제 성공")
  void deleteFriend_success() {
    // Given
    DeleteRequest request = DeleteRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity myFriendAcceptEntity = FriendEntity.builder()
        .id(1L)
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(userEntity)
        .friendUser(friendUserEntity1)
        .build();

    FriendEntity otherFriendAcceptEntity = FriendEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(friendUserEntity1)
        .friendUser(userEntity)
        .build();

    FriendEntity expectedMyDeleteFriendEntity = FriendEntity.builder()
        .id(1L)
        .status(DELETE)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .deletedDateTime(fixedDeleteDateTime)
        .user(userEntity)
        .friendUser(friendUserEntity1)
        .build();

    FriendEntity expectedOtherDeleteFriendEntity = FriendEntity.builder()
        .id(2L)
        .status(DELETE)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .deletedDateTime(fixedDeleteDateTime)
        .user(friendUserEntity1)
        .friendUser(userEntity)
        .build();

    Instant fixedInstant = fixedRejectDateTime.atZone(ZoneId.systemDefault())
        .toInstant();

    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    FriendEntity myDeleteFriendEntity =
        DeleteRequest.toMyFriendEntity(myFriendAcceptEntity, clock);

    FriendEntity otherDeleteFriendEntity =
        DeleteRequest.toOtherFriendEntity(myDeleteFriendEntity, otherFriendAcceptEntity);

    getCurrentUser(userEntity);

    when(friendRepository.findByIdAndStatus
        (request.getFriendId(), ACCEPT))
        .thenReturn(Optional.of(myFriendAcceptEntity));

    when(friendRepository.findByFriendUserIdAndUserIdAndStatus(
        userEntity.getId(),
        myFriendAcceptEntity.getFriendUser().getId(),
        ACCEPT))
        .thenReturn(Optional.of(otherFriendAcceptEntity));

    when(friendRepository.save(myDeleteFriendEntity)).thenReturn(myDeleteFriendEntity);
    when(friendRepository.save(otherDeleteFriendEntity)).thenReturn(otherDeleteFriendEntity);

    // when
    friendService.deleteFriend(request);

    // Then
    assertEquals(expectedMyDeleteFriendEntity, myDeleteFriendEntity);
    assertEquals(expectedOtherDeleteFriendEntity, otherDeleteFriendEntity);
  }

  @Test
  @DisplayName("친구 삭제 실패 : 내가 받은 친구가 아닐때")
  void deleteFriend_failIfNotMyReceiveFriendAccept() {
    // Given
    DeleteRequest request = DeleteRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity selfEntity = FriendEntity.builder()
        .id(1L)
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(friendUserEntity1)
        .friendUser(friendUserEntity1)
        .build();

    getCurrentUser(userEntity);

    when(friendRepository.findByIdAndStatus(request.getFriendId(), ACCEPT))
        .thenReturn(Optional.ofNullable(selfEntity));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.deleteFriend(request);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_ACCEPT, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 검색 성공")
  void searchNickName_success() {
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

    getCurrentUser(userEntity);

    when(friendCustomRepository.findBySearchFriendList
        (userEntity.getId(), nickName, pageable))
        .thenReturn(searchResponsePage);

    // when
    Page<FriendListResponse> result = friendService.searchNickName(nickName, pageable);

    // Then
    assertEquals(searchResponsePage, result);
  }

  @Test
  @DisplayName("친구 검색 실패 : 검색할 닉네임을 입력 안했을때")
  void searchNickName_failIfNickNameIsBlank() {
    // Given
    String nickName = "";
    Pageable pageable = PageRequest.of(0, 10);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.searchNickName(nickName, pageable);
    });

    // then
    assertEquals(ErrorCode.NOT_FOUND_NICKNAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 리스트 조회 성공")
  void getMyFriends_success() {
    // Given
    Pageable pageable = PageRequest.of(0, 1);

    FriendEntity friendEntity1 = FriendEntity.builder()
        .id(1L)
        .status(ACCEPT)
        .user(userEntity)
        .friendUser(friendUserEntity1)
        .build();

    List<FriendEntity> friendEntityList =
        List.of(friendEntity1);

    Page<FriendEntity> searchResponsePage =
        new PageImpl<>(friendEntityList, pageable, 1);

    List<FriendListResponse> listResponseFriendList = searchResponsePage.stream()
        .map(FriendListResponse::toDto)
        .toList();

    getCurrentUser(userEntity);

    when(friendRepository.findByStatusAndUserId
        (ACCEPT, userEntity.getId(), pageable))
        .thenReturn(searchResponsePage);

    // when
    List<FriendListResponse> result = friendService.getMyFriends(pageable);

    // Then
    assertEquals(listResponseFriendList, result);
  }

  @Test
  @DisplayName("경기 초대 친구 리스트 조회")
  void getMyInviteList_success() {
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

    getCurrentUser(userEntity);

    when(friendCustomRepository.findByMyInviteFriendList
        (userEntity.getId(), gameId, pageable))
        .thenReturn(inviteListResponsePage);

    // when
    Page<InviteFriendListResponse> result = friendService.getMyInviteList(gameId, pageable);
    List<InviteFriendListResponse> responseList = result.getContent();

    // Then
    assertEquals(inviteListResponseFriendList, responseList);
  }

  @Test
  @DisplayName("내가 친구 요청 받은 리스트 조회 성공")
  void getRequestFriendList_success() {
    // Given

    FriendEntity friendEntity1 = FriendEntity.builder()
        .id(1L)
        .status(ACCEPT)
        .user(friendUserEntity1)
        .friendUser(userEntity)
        .build();

    FriendEntity friendEntity2 = FriendEntity.builder()
        .id(2L)
        .status(ACCEPT)
        .user(friendUserEntity2)
        .friendUser(userEntity)
        .build();

    RequestFriendListResponse requestFriendListResponse1 = RequestFriendListResponse.builder()
        .userId(2L)
        .birthday(LocalDate.of(1990, 1, 1))
        .nickName("test1")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .mannerPoint(null)
        .friendId(1L)
        .build();

    RequestFriendListResponse requestFriendListResponse2 = RequestFriendListResponse.builder()
        .userId(3L)
        .birthday(LocalDate.of(1990, 1, 1))
        .nickName("test2")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .mannerPoint(null)
        .friendId(1L)
        .build();

    List<FriendEntity> friendEntityList =
        List.of(friendEntity1, friendEntity2);

    List<RequestFriendListResponse> expectedRequestFriendList
        = friendEntityList.stream()
        .map(RequestFriendListResponse::toDto)
        .toList();

    getCurrentUser(userEntity);

    when(friendRepository.findByStatusAndFriendUserId
        (FriendStatus.APPLY, userEntity.getId()))
        .thenReturn(friendEntityList);

    // when
    List<RequestFriendListResponse> requestFriendList = friendService.getRequestFriendList();

    // Then
    assertEquals(expectedRequestFriendList, requestFriendList);
  }

  private void getCurrentUser(UserEntity userEntity) {
    when(jwtTokenExtract.currentUser()).thenReturn(userEntity);

    when(userRepository.findById(eq(userEntity.getId()))).thenReturn(
        Optional.of(userEntity));
  }

}