package com.zerobase.hoops.friends.service;

import static com.zerobase.hoops.friends.type.FriendStatus.ACCEPT;
import static com.zerobase.hoops.friends.type.FriendStatus.APPLY;
import static com.zerobase.hoops.friends.type.FriendStatus.CANCEL;
import static com.zerobase.hoops.friends.type.FriendStatus.DELETE;
import static com.zerobase.hoops.friends.type.FriendStatus.REJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.alarm.repository.EmitterRepository;
import com.zerobase.hoops.alarm.repository.NotificationRepository;
import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.document.FriendDocument;
import com.zerobase.hoops.document.UserDocument;
import com.zerobase.hoops.exception.CustomException;
import com.zerobase.hoops.exception.ErrorCode;
import com.zerobase.hoops.friends.dto.AcceptFriendDto;
import com.zerobase.hoops.friends.dto.ApplyFriendDto;
import com.zerobase.hoops.friends.dto.CancelFriendDto;
import com.zerobase.hoops.friends.dto.DeleteFriendDto;
import com.zerobase.hoops.friends.dto.FriendListDto;
import com.zerobase.hoops.friends.dto.InviteFriendListDto;
import com.zerobase.hoops.friends.dto.RejectFriendDto;
import com.zerobase.hoops.friends.dto.RequestFriendListDto;
import com.zerobase.hoops.friends.dto.SearchFriendListDto;
import com.zerobase.hoops.friends.repository.FriendRepository;
import com.zerobase.hoops.friends.repository.impl.FriendCustomRepositoryImpl;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.invite.type.InviteStatus;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

  private UserDocument user;
  private UserDocument friendUser1;
  private UserDocument friendUser2;
  private FriendDocument expectedApplyFriendDocument;
  private OffsetDateTime fixedCreateDateTime;
  private OffsetDateTime fixedCancelDateTime;
  private OffsetDateTime fixedAcceptDateTime;
  private OffsetDateTime fixedRejectDateTime;
  private OffsetDateTime fixedDeleteDateTime;

  @BeforeEach
  void setUp() throws Exception{
    fixedCreateDateTime = OffsetDateTime.of(LocalDateTime.of(2024, 6, 9, 0, 0,
        0), ZoneOffset.ofHours(9));
    fixedCancelDateTime = OffsetDateTime.of(LocalDateTime.of(2024, 6, 10, 0, 0,
        0), ZoneOffset.ofHours(9));
    fixedAcceptDateTime = OffsetDateTime.of(LocalDateTime.of(2024, 6, 10, 0, 0,
        0), ZoneOffset.ofHours(9));
    fixedRejectDateTime = OffsetDateTime.of(LocalDateTime.of(2024, 6, 10, 0, 0,
        0), ZoneOffset.ofHours(9));
    fixedDeleteDateTime = OffsetDateTime.of(LocalDateTime.of(2024, 6, 10, 0, 0,
        0), ZoneOffset.ofHours(9));
    user = UserDocument.builder()
        .id("1")
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
    friendUser1 = UserDocument.builder()
        .id("2")
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
    friendUser2 = UserDocument.builder()
        .id("3")
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
    expectedApplyFriendDocument = FriendDocument.builder()
        .id("1")
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
    ApplyFriendDto.Request request = ApplyFriendDto.Request.builder()
        .friendUserId("2")
        .build();

    Instant fixedInstant = fixedCreateDateTime.toInstant();
    // Clock의 instant()와 getZone() 메서드를 설정
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneOffset.ofHours(9));

    FriendDocument applyFriendDocument = new ApplyFriendDto.Request()
        .toDocument(user, friendUser1, 1L, clock);

    // 친구 신청, 수락 상태가 없다고 가정
    existsApplyOrAcceptFriend(user.getId(), request.getFriendUserId(), false);
    
    // 자신 친구가 10명이 있다고 가정
    countsFriend(user.getId(), 10);

    // 상대방 친구가 10명이 있다고 가정
    countsFriend(request.getFriendUserId(), 10);

    // 친구 유저 조회
    getFriendUser(request.getFriendUserId(), friendUser1);

    when(friendRepository.count()).thenReturn(0L);

    when(friendRepository.save(applyFriendDocument))
        .thenReturn(applyFriendDocument);

    // when
    friendService.validApplyFriend(request, user);

    // Then
    assertEquals(expectedApplyFriendDocument, applyFriendDocument);

  }

  @Test
  @DisplayName("친구 신청 실패 : 자기 자신을 친구 신청 할때")
  void testApplyFriendFailIfSelfFriendApply() {
    // Given
    ApplyFriendDto.Request request = ApplyFriendDto.Request.builder()
        .friendUserId("1")
        .build();

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validApplyFriend(request, user);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_FRIEND, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 실패 : 이미 친구 신청, 수락 상태 일때")
  void testApplyFriendFailIfAlreadyApplyOrAccept() {
    // Given
    ApplyFriendDto.Request request = ApplyFriendDto.Request.builder()
        .friendUserId("2")
        .build();

    // 친구 신청, 수락 상태가 있다고 가정
    existsApplyOrAcceptFriend(user.getId(), request.getFriendUserId(), true);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validApplyFriend(request, user);
    });

    // then
    assertEquals(ErrorCode.ALREADY_APPLY_ACCEPT_STATUS, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 실패 : 자신 친구가 30명 일때")
  void testApplyFriendFailIfMyFriendFull() {
    // Given
    ApplyFriendDto.Request request = ApplyFriendDto.Request.builder()
        .friendUserId("2")
        .build();

    // 친구 신청, 수락 상태가 없다고 가정
    existsApplyOrAcceptFriend(user.getId(), request.getFriendUserId(), false);

    // 자신 친구가 30명이 있다고 가정
    countsFriend(user.getId(), 30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validApplyFriend(request, user);
    });

    // then
    assertEquals(ErrorCode.SELF_FRIEND_FULL, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 실패 : 상대방 친구가 30명 일때")
  void testApplyFriendFailIfOtherFriendFull() {
    // Given
    ApplyFriendDto.Request request = ApplyFriendDto.Request.builder()
        .friendUserId("2")
        .build();

    // 친구 신청, 수락 상태가 없다고 가정
    existsApplyOrAcceptFriend(user.getId(), request.getFriendUserId(), false);

    // 자신 친구가 30명이 있다고 가정
    countsFriend(user.getId(), 10);

    countsFriend(request.getFriendUserId(), 30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validApplyFriend(request, user);
    });

    // then
    assertEquals(ErrorCode.OTHER_FRIEND_FULL, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 신청 취소 성공")
  void testCancelFriendSuccess() {
    // Given
    CancelFriendDto.Request request = CancelFriendDto.Request.builder()
        .friendId("1")
        .build();

    FriendDocument expectedCancelFriendDocument = FriendDocument.builder()
        .id(expectedApplyFriendDocument.getId())
        .status(CANCEL)
        .createdDateTime(fixedCreateDateTime)
        .canceledDateTime(fixedCancelDateTime)
        .user(user)
        .friendUser(friendUser1)
        .build();

    Instant fixedInstant = fixedCancelDateTime.toInstant();
    // Clock의 instant()와 getZone() 메서드를 설정
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneOffset.ofHours(9));

    FriendDocument cancelDocument = FriendDocument.setCancel(
        expectedApplyFriendDocument, clock);

    // 친구 신청 entity 조회
    getFriendDocument(request.getFriendId());

    when(friendRepository.save(cancelDocument)).thenReturn(cancelDocument);

    // when
    friendService.validCancelFriend(request, user);

    // Then
    assertEquals(expectedCancelFriendDocument, cancelDocument);
  }

  @Test
  @DisplayName("친구 신청 취소 실패 : 자기 자신이 한 친구 신청이 아님")
  void testCancelFriendFailIfNotSelfFriendApply() {
    // Given
    CancelFriendDto.Request request = CancelFriendDto.Request.builder()
        .friendId("1")
        .build();

    expectedApplyFriendDocument = FriendDocument.builder()
        .id("1")
        .user(friendUser1)
        .build();

    // 친구 신청 entity 조회
    getFriendDocument(request.getFriendId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validCancelFriend(request, user);
    });

    // then
    assertEquals(ErrorCode.NOT_SELF_APPLY, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 수락 성공")
  void testAcceptFriendSuccess() {
    // Given
    AcceptFriendDto.Request request = AcceptFriendDto.Request.builder()
        .friendId("1")
        .build();

    FriendDocument expectedMyAcceptFriendDocument = FriendDocument.builder()
        .id(expectedApplyFriendDocument.getId())
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(user)
        .friendUser(friendUser1)
        .build();

    FriendDocument expectedOtherAcceptFriendDocument = FriendDocument.builder()
        .id("2")
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(friendUser1)
        .friendUser(user)
        .build();

    Instant fixedInstant = fixedAcceptDateTime.toInstant();
    // Clock의 instant()와 getZone() 메서드를 설정
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneOffset.ofHours(9));

    FriendDocument myFriendDocument = FriendDocument.setAcceptMyFriend(
        expectedApplyFriendDocument, clock);

    FriendDocument otherFriendDocument
        = FriendDocument.setAcceptOtherFriend(myFriendDocument, 2L);

    // 친구 신청 entity 조회
    getFriendDocument(request.getFriendId());

    // 자신의 친구가 10명이 있다고 가정
    countsFriend(expectedApplyFriendDocument.getFriendUser().getId(),10);

    // 상대방의 친구가 10명이 있다고 가정
    countsFriend(expectedApplyFriendDocument.getUser().getId(),10);

    when(friendRepository.count()).thenReturn(1L);

    when(friendRepository.save(myFriendDocument)).thenReturn(myFriendDocument);

    when(friendRepository.save(otherFriendDocument)).thenReturn(otherFriendDocument);

    // when
    friendService.validAcceptFriend(request, friendUser1);

    // Then
    assertEquals(expectedMyAcceptFriendDocument, myFriendDocument);
    assertEquals(expectedOtherAcceptFriendDocument, otherFriendDocument);
  }

  @Test
  @DisplayName("친구 수락 실패 : 자신이 받은 친구 신청이 아님")
  void testAcceptFriendFailIfNotSelfReceiveFriendApply() {
    // Given
    AcceptFriendDto.Request request = AcceptFriendDto.Request.builder()
        .friendId("1")
        .build();

    expectedApplyFriendDocument = FriendDocument.builder()
        .id("1")
        .friendUser(user)
        .build();

    // 친구 신청 entity 조회
    getFriendDocument(request.getFriendId());

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validAcceptFriend(request, friendUser1);
    });

    // then
    assertEquals(ErrorCode.NOT_MY_RECEIVE, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 수락 실패 : 자신 친구 목록이 30명 일때")
  void testAcceptFriendFailIfMyFriendFull() {
    // Given
    AcceptFriendDto.Request request = AcceptFriendDto.Request.builder()
        .friendId("1")
        .build();

    // 친구 신청 entity 조회
    getFriendDocument(request.getFriendId());

    // 자신의 친구가 10명이 있다고 가정
    countsFriend(expectedApplyFriendDocument.getFriendUser().getId(),30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validAcceptFriend(request, friendUser1);
    });

    // then
    assertEquals(ErrorCode.SELF_FRIEND_FULL, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 수락 실패 : 상대방 친구 목록이 30명 일때")
  void testAcceptFriendFailIfOtherFriendFull() {
    // Given
    AcceptFriendDto.Request request = AcceptFriendDto.Request.builder()
        .friendId("1")
        .build();

    // 친구 신청 entity 조회
    getFriendDocument(request.getFriendId());

    // 자신의 친구가 10명이 있다고 가정
    countsFriend(expectedApplyFriendDocument.getFriendUser().getId(),10);

    // 상대방의 친구가 10명이 있다고 가정
    countsFriend(expectedApplyFriendDocument.getUser().getId(),30);

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validAcceptFriend(request, friendUser1);
    });

    // then
    assertEquals(ErrorCode.OTHER_FRIEND_FULL, exception.getErrorCode());
  }


  @Test
  @DisplayName("친구 거절 성공")
  void testRejectFriendSuccess() {
    // Given
    RejectFriendDto.Request request = RejectFriendDto.Request.builder()
        .friendId("1")
        .build();

    FriendDocument expectedRejectFriendDocument = FriendDocument.builder()
        .id(expectedApplyFriendDocument.getId())
        .status(REJECT)
        .createdDateTime(fixedCreateDateTime)
        .rejectedDateTime(fixedRejectDateTime)
        .user(user)
        .friendUser(friendUser1)
        .build();

    Instant fixedInstant = fixedRejectDateTime.toInstant();
    // Clock의 instant()와 getZone() 메서드를 설정
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneOffset.ofHours(9));

    FriendDocument rejectFriendDocument = FriendDocument.setReject(
        expectedApplyFriendDocument, clock);

    // 친구 신청 entity 조회
    getFriendDocument(request.getFriendId());

    when(friendRepository.save(rejectFriendDocument)).thenReturn(rejectFriendDocument);

    // when
    friendService.validRejectFriend(request, friendUser1);

    // Then
    assertEquals(expectedRejectFriendDocument, rejectFriendDocument);
  }

  @Test
  @DisplayName("친구 거절 실패 : 자신이 받은 친구 신청이 아님")
  void testRejectFriendFailIfNotSelfReceiveFriendApply() {
    // Given
    RejectFriendDto.Request request = RejectFriendDto.Request.builder()
        .friendId("1")
        .build();

    expectedApplyFriendDocument = FriendDocument.builder()
        .id("1")
        .friendUser(user)
        .build();

    when(friendRepository.findByIdAndStatus(request.getFriendId(), APPLY))
        .thenReturn(Optional.ofNullable(expectedApplyFriendDocument));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validRejectFriend(request, friendUser1);
    });

    // then
    assertEquals(ErrorCode.NOT_MY_RECEIVE, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 삭제 성공")
  void testDeleteFriendSuccess() {
    // Given
    DeleteFriendDto.Request request = DeleteFriendDto.Request.builder()
        .friendId("1")
        .build();

    FriendDocument myFriendAcceptDocument = FriendDocument.builder()
        .id("1")
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(user)
        .friendUser(friendUser1)
        .build();

    FriendDocument otherFriendAcceptDocument = FriendDocument.builder()
        .id("2")
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(friendUser1)
        .friendUser(user)
        .build();

    FriendDocument expectedMyDeleteFriendDocument = FriendDocument.builder()
        .id("1")
        .status(DELETE)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .deletedDateTime(fixedDeleteDateTime)
        .user(user)
        .friendUser(friendUser1)
        .build();

    FriendDocument expectedOtherDeleteFriendDocument = FriendDocument.builder()
        .id("2")
        .status(DELETE)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .deletedDateTime(fixedDeleteDateTime)
        .user(friendUser1)
        .friendUser(user)
        .build();

    Instant fixedInstant = fixedRejectDateTime.toInstant();
    // Clock의 instant()와 getZone() 메서드를 설정
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneOffset.ofHours(9));

    FriendDocument myDeleteFriendDocument =
        FriendDocument.setDeleteMyFriend(myFriendAcceptDocument, clock);

    FriendDocument otherDeleteFriendDocument =
        FriendDocument.setDeleteOtherFriend(myDeleteFriendDocument, otherFriendAcceptDocument);

    when(friendRepository.findByIdAndStatus
        (request.getFriendId(), ACCEPT))
        .thenReturn(Optional.of(myFriendAcceptDocument));

    when(friendRepository.findByFriendUserIdAndUserIdAndStatus(
        user.getId(), myFriendAcceptDocument.getFriendUser().getId(), ACCEPT))
        .thenReturn(Optional.of(otherFriendAcceptDocument));

    when(friendRepository.save(myDeleteFriendDocument)).thenReturn(myDeleteFriendDocument);
    when(friendRepository.save(otherDeleteFriendDocument)).thenReturn(otherDeleteFriendDocument);

    // when
    friendService.validDeleteFriend(request, user);

    // Then
    assertEquals(expectedMyDeleteFriendDocument, myDeleteFriendDocument);
    assertEquals(expectedOtherDeleteFriendDocument, otherDeleteFriendDocument);
  }

  @Test
  @DisplayName("친구 삭제 실패 : 내가 받은 친구가 아닐때")
  void testDeleteFriendFailIfNotMyReceiveFriendAccept() {
    // Given
    DeleteFriendDto.Request request = DeleteFriendDto.Request.builder()
        .friendId("1")
        .build();

    FriendDocument selfDocument = FriendDocument.builder()
        .id("1")
        .status(ACCEPT)
        .createdDateTime(fixedCreateDateTime)
        .acceptedDateTime(fixedAcceptDateTime)
        .user(friendUser1)
        .friendUser(friendUser1)
        .build();

    when(friendRepository.findByIdAndStatus(request.getFriendId(), ACCEPT))
        .thenReturn(Optional.ofNullable(selfDocument));

    // when
    CustomException exception = assertThrows(CustomException.class, () -> {
      friendService.validDeleteFriend(request, user);
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

    SearchFriendListDto.Response friendListResponse1 = SearchFriendListDto.Response.builder()
        .userId("2")
        .birthday(LocalDate.of(1990, 1, 1))
        .nickName("test1")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .friendId("1")
        .build();

    SearchFriendListDto.Response friendListResponse2 = SearchFriendListDto.Response.builder()
        .userId("3")
        .birthday(LocalDate.of(1990, 1, 1))
        .nickName("test2")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .friendId(null)
        .build();

    List<SearchFriendListDto.Response> listResponseFriendList =
        List.of(friendListResponse1, friendListResponse2);

    Page<SearchFriendListDto.Response> searchResponsePage =
        new PageImpl<>(listResponseFriendList, pageable, 2);

    when(friendCustomRepository.findBySearchFriendList
        (user.getId(), nickName, pageable)).thenReturn(searchResponsePage);

    // when
    Page<SearchFriendListDto.Response> result =
        friendService.validSearchFriend(nickName, pageable, user);

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
      friendService.validSearchFriend(nickName, pageable, user);
    });

    // then
    assertEquals(ErrorCode.NOT_FOUND_NICKNAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("친구 리스트 조회 성공")
  void testGetMyFriendsSuccess() {
    // Given
    Pageable pageable = PageRequest.of(0, 1);

    FriendDocument friendDocument1 = FriendDocument.builder()
        .id("1")
        .status(ACCEPT)
        .user(user)
        .friendUser(friendUser1)
        .build();

    List<FriendDocument> friendDocumentList =
        List.of(friendDocument1);

    Page<FriendDocument> searchResponsePage =
        new PageImpl<>(friendDocumentList, pageable, 1);

    List<FriendListDto.Response> listResponseFriendList = searchResponsePage.stream()
        .map(FriendListDto.Response::toDto)
        .toList();

    when(friendRepository.findByStatusAndUserId
        (ACCEPT, user.getId(), pageable))
        .thenReturn(searchResponsePage);

    // when
    List<FriendListDto.Response> result =
        friendService.validGetMyFriendList(pageable, user);

    // Then
    assertEquals(listResponseFriendList, result);
  }

  @Test
  @DisplayName("경기 초대 친구 리스트 조회")
  void testGetMyInviteListSuccess() {
    // Given
    String gameId = "1";
    Pageable pageable = PageRequest.of(0, 1);

    InviteFriendListDto.Response inviteFriendListResponse = InviteFriendListDto
        .Response.builder()
        .userId("2")
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("test1")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .mannerPoint("0")
        .status(InviteStatus.REQUEST)
        .build();

    List<InviteFriendListDto.Response> inviteListResponseFriendList =
        List.of(inviteFriendListResponse);

    Page<InviteFriendListDto.Response> inviteListResponsePage =
        new PageImpl<>(inviteListResponseFriendList, pageable, 1);

    when(friendCustomRepository.findByMyInviteFriendList
        (user.getId(), gameId, pageable))
        .thenReturn(inviteListResponsePage);

    // when
    Page<InviteFriendListDto.Response> result =
        friendService.validGetMyInviteFriendList(gameId, pageable, user);

    // Then
    assertEquals(inviteListResponsePage, result);
  }

  @Test
  @DisplayName("내가 친구 요청 받은 리스트 조회 성공")
  void testGetRequestFriendListSuccess() {
    // Given
    Pageable pageable = PageRequest.of(0, 1);

    FriendDocument friendDocument1 = FriendDocument.builder()
        .id("1")
        .status(ACCEPT)
        .user(friendUser1)
        .friendUser(user)
        .build();

    FriendDocument friendDocument2 = FriendDocument.builder()
        .id("2")
        .status(ACCEPT)
        .user(friendUser2)
        .friendUser(user)
        .build();

    List<FriendDocument> friendDocumentList =
        List.of(friendDocument1, friendDocument2);

    Page<FriendDocument> friendDocumentPage =
        new PageImpl<>(friendDocumentList, pageable, 1);

    List<RequestFriendListDto.Response> expectedRequestFriendList
        = friendDocumentList.stream()
        .map(RequestFriendListDto.Response::toDto)
        .toList();

    when(friendRepository.findByStatusAndFriendUserId
        (FriendStatus.APPLY, user.getId(), pageable))
        .thenReturn(friendDocumentPage);

    // when
    List<RequestFriendListDto.Response> requestFriendList
        = friendService.validGetRequestFriendList(pageable, user);

    // Then
    assertEquals(expectedRequestFriendList, requestFriendList);
  }

  // 친구 신청, 수락 상태 검사
  private void existsApplyOrAcceptFriend(String userId, String friendUserId,
      boolean flag) {
    when(friendRepository.existsByUserIdAndFriendUserIdAndStatusIn(
        userId, friendUserId, List.of(APPLY, ACCEPT))).thenReturn(flag);
  }

  // 친구 몇명인지 계산
  private void countsFriend(String userId, int count) {
    when(friendRepository.countByUserIdAndStatus(userId, ACCEPT))
        .thenReturn(count);
  }

  // 친구 조회
  private void getFriendUser(String userId, UserDocument user) {
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
  }

  // 친구 신청 entity 조회
  private void getFriendDocument(String friendId) {
    when(friendRepository.findByIdAndStatus(friendId, APPLY))
        .thenReturn(Optional.of(expectedApplyFriendDocument));
  }

}