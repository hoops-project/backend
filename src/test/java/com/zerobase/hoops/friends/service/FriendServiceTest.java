package com.zerobase.hoops.friends.service;

import static com.zerobase.hoops.friends.type.FriendStatus.ACCEPT;
import static com.zerobase.hoops.friends.type.FriendStatus.APPLY;
import static com.zerobase.hoops.friends.type.FriendStatus.CANCEL;
import static com.zerobase.hoops.friends.type.FriendStatus.DELETE;
import static com.zerobase.hoops.friends.type.FriendStatus.REJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.alarm.repository.EmitterRepository;
import com.zerobase.hoops.alarm.repository.NotificationRepository;
import com.zerobase.hoops.alarm.service.NotificationService;
import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.NotificationEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.dto.FriendDto.AcceptRequest;
import com.zerobase.hoops.friends.dto.FriendDto.AcceptResponse;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyRequest;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyResponse;
import com.zerobase.hoops.friends.dto.FriendDto.CancelRequest;
import com.zerobase.hoops.friends.dto.FriendDto.CancelResponse;
import com.zerobase.hoops.friends.dto.FriendDto.DeleteRequest;
import com.zerobase.hoops.friends.dto.FriendDto.DeleteResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RejectRequest;
import com.zerobase.hoops.friends.dto.FriendDto.RejectResponse;
import com.zerobase.hoops.friends.dto.FriendDto.ListResponse;
import com.zerobase.hoops.friends.dto.FriendDto.RequestListResponse;
import com.zerobase.hoops.friends.repository.FriendRepository;
import com.zerobase.hoops.friends.repository.impl.FriendCustomRepositoryImpl;
import com.zerobase.hoops.friends.type.FriendStatus;
import com.zerobase.hoops.security.JwtTokenExtract;
import com.zerobase.hoops.users.repository.UserRepository;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

  private UserEntity userEntity;
  private UserEntity friendUserEntity1;

  private UserEntity friendUserEntity2;

  private FriendEntity friendEntity;

  private NotificationEntity notificationEntity;

  @BeforeEach
  void setUp() {
    userEntity = UserEntity.builder()
        .userId(1L)
        .id("test")
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
        .userId(2L)
        .id("test1")
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
        .userId(3L)
        .id("test2")
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
    friendEntity = FriendEntity.builder()
        .friendId(1L)
        .status(APPLY)
        .createdDateTime(LocalDateTime.of(2024, 5, 25, 0, 0, 0))
        .userEntity(userEntity)
        .friendUserEntity(friendUserEntity1)
        .build();
    notificationEntity = NotificationEntity.builder()
        .receiver(friendUserEntity1)
        .content("테스트내용")
        .createdDateTime(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("친구 신청 성공")
  void applyFriend_success() {
    // Given
    ApplyRequest request = ApplyRequest.builder()
        .friendUserId(2L)
        .build();

    getCurrentUser();

    // 친구 신청, 수락 상태가 없다고 가정
    when(friendRepository.countByFriendUserEntityUserIdAndStatusIn
            (anyLong(), any()))
        .thenReturn(0);
    
    // 자신 및 상대방 친구목록에 10명이 있다고 가정
    when(friendRepository.countByUserEntityUserIdAndStatus
        (anyLong(), eq(ACCEPT)))
        .thenReturn(10)
        .thenReturn(10);

    when(userRepository.findById(2L)).thenReturn(Optional.of(friendUserEntity1));

    lenient().when(notificationRepository.save(any())).thenReturn(notificationEntity);

    lenient().
        when(emitterRepository.findAllStartWithByUserId(anyString())).thenReturn(null);

    when(friendRepository.save(any())).thenAnswer(invocation -> {
      FriendEntity savedFriendEntity = invocation.getArgument(0);
      savedFriendEntity.setFriendId(1L); // friendId 동적 할당
      return savedFriendEntity;
    });

    // when
    ApplyResponse response = friendService.applyFriend(request);

    // Then
    assertEquals(friendEntity.getFriendId(), response.getFriendId());
    assertEquals(friendEntity.getStatus(),
        response.getStatus());
    assertEquals(friendEntity.getUserEntity().getNickName(),
        response.getNickName());
    assertEquals(friendEntity.getFriendUserEntity().getNickName(),
        response.getFriendNickName());

  }

  @Test
  @DisplayName("친구 신청 취소 성공")
  void cancelFriend_success() {
    // Given
    CancelRequest request = CancelRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity cancelEntity = FriendEntity.builder()
        .friendId(1L)
        .status(CANCEL)
        .createdDateTime(LocalDateTime.of(2024, 5, 25, 0, 0, 0))
        .canceledDateTime(LocalDateTime.of(2024, 5, 25, 8, 0, 0))
        .userEntity(userEntity)
        .friendUserEntity(friendUserEntity1)
        .build();

    getCurrentUser();

    when(friendRepository.findByFriendIdAndStatus(any(), eq(APPLY)))
        .thenReturn(Optional.of(friendEntity));

    when(friendRepository.save(any())).thenReturn(cancelEntity);

    // when
    CancelResponse response = friendService.cancelFriend(request);

    // Then
    assertEquals(cancelEntity.getFriendId(), response.getFriendId());
    assertEquals(cancelEntity.getStatus(), response.getStatus());
    assertEquals(cancelEntity.getUserEntity().getNickName(),
        response.getNickName());
    assertEquals(cancelEntity.getFriendUserEntity().getNickName(),
        response.getFriendNickName());

  }

  @Test
  @DisplayName("친구 수락 성공")
  void acceptFriend_success() {
    // Given
    AcceptRequest request = AcceptRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity selfEntity = FriendEntity.builder()
        .friendId(1L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 5, 25, 0, 0, 0))
        .acceptedDateTime(LocalDateTime.of(2024, 5, 25, 8, 0, 0))
        .userEntity(userEntity)
        .friendUserEntity(friendUserEntity1)
        .build();

    FriendEntity otherEntity = FriendEntity.builder()
        .friendId(2L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 5, 25, 0, 0, 0))
        .acceptedDateTime(LocalDateTime.of(2024, 5, 25, 8, 0, 0))
        .userEntity(friendUserEntity1)
        .friendUserEntity(userEntity)
        .build();

    when(jwtTokenExtract.currentUser()).thenReturn(friendUserEntity1);

    when(userRepository.findById(2L)).thenReturn(
        Optional.ofNullable(friendUserEntity1));

    when(friendRepository.findByFriendIdAndStatus(anyLong(), eq(APPLY)))
        .thenReturn(Optional.ofNullable(friendEntity));

    // 자신 또는 상대방의 친구 목록에 10명이 있다고 가정
    when(friendRepository.countByUserEntityUserIdAndStatus
        (anyLong(), eq(ACCEPT))).thenReturn(10).thenReturn(10);

    when(friendRepository.save(any())).thenReturn(selfEntity)
        .thenAnswer(invocation -> {
      FriendEntity savedFriendEntity = invocation.getArgument(0);
      savedFriendEntity.setFriendId(2L); // friendId 동적 할당
      return savedFriendEntity;
    });

    // when
    List<AcceptResponse> result = friendService.acceptFriend(request);

    // Then
    assertEquals(selfEntity.getFriendId(), result.get(0).getFriendId());
    assertEquals(selfEntity.getStatus(), result.get(0).getStatus());
    assertEquals(selfEntity.getUserEntity().getNickName(),
        result.get(0).getNickName());
    assertEquals(selfEntity.getFriendUserEntity().getNickName(),
        result.get(0).getFriendNickName());

    assertEquals(otherEntity.getFriendId(), result.get(1).getFriendId());
    assertEquals(otherEntity.getStatus(), result.get(1).getStatus());
    assertEquals(otherEntity.getUserEntity().getNickName(),
        result.get(1).getNickName());
    assertEquals(otherEntity.getFriendUserEntity().getNickName(),
        result.get(1).getFriendNickName());

  }

  @Test
  @DisplayName("친구 거절 성공")
  void rejectFriend_success() {
    // Given
    RejectRequest request = RejectRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity rejectEntity = FriendEntity.builder()
        .friendId(1L)
        .status(REJECT)
        .createdDateTime(LocalDateTime.of(2024, 5, 25, 0, 0, 0))
        .rejectedDateTime(LocalDateTime.of(2024, 5, 25, 12, 0, 0))
        .userEntity(userEntity)
        .friendUserEntity(friendUserEntity1)
        .build();


    when(jwtTokenExtract.currentUser()).thenReturn(friendUserEntity1);

    when(userRepository.findById(2L)).thenReturn(
        Optional.ofNullable(friendUserEntity1));

    when(friendRepository.findByFriendIdAndStatus(anyLong(), eq(APPLY)))
        .thenReturn(Optional.ofNullable(friendEntity));


    when(friendRepository.save(any())).thenReturn(rejectEntity);

    // when
    RejectResponse result = friendService.rejectFriend(request);

    // Then
    assertEquals(rejectEntity.getFriendId(), result.getFriendId());
    assertEquals(rejectEntity.getStatus(), result.getStatus());
    assertEquals(rejectEntity.getUserEntity().getNickName(), result.getNickName());
    assertEquals(rejectEntity.getFriendUserEntity().getNickName(),
        result.getFriendNickName());

  }

  @Test
  @DisplayName("친구 삭제 성공")
  void deleteFriend_success() {
    // Given
    DeleteRequest request = DeleteRequest.builder()
        .friendId(1L)
        .build();

    FriendEntity selfEntity = FriendEntity.builder()
        .friendId(1L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 5, 25, 0, 0, 0))
        .acceptedDateTime(LocalDateTime.of(2024, 5, 25, 8, 0, 0))
        .userEntity(userEntity)
        .friendUserEntity(friendUserEntity1)
        .build();

    FriendEntity otherEntity = FriendEntity.builder()
        .friendId(2L)
        .status(ACCEPT)
        .createdDateTime(LocalDateTime.of(2024, 5, 25, 0, 0, 0))
        .acceptedDateTime(LocalDateTime.of(2024, 5, 25, 8, 0, 0))
        .userEntity(friendUserEntity1)
        .friendUserEntity(userEntity)
        .build();

    FriendEntity selfDeleteEntity = FriendEntity.builder()
        .friendId(1L)
        .status(DELETE)
        .createdDateTime(LocalDateTime.of(2024, 5, 25, 0, 0, 0))
        .acceptedDateTime(LocalDateTime.of(2024, 5, 25, 8, 0, 0))
        .deletedDateTime(LocalDateTime.of(2024, 5, 25, 12, 0, 0))
        .userEntity(userEntity)
        .friendUserEntity(friendUserEntity1)
        .build();

    FriendEntity otherDeleteEntity = FriendEntity.builder()
        .friendId(2L)
        .status(DELETE)
        .createdDateTime(LocalDateTime.of(2024, 5, 25, 0, 0, 0))
        .rejectedDateTime(LocalDateTime.of(2024, 5, 25, 12, 0, 0))
        .userEntity(friendUserEntity1)
        .friendUserEntity(userEntity)
        .build();

    getCurrentUser();

    when(friendRepository.findByFriendIdAndStatus
        (anyLong(), eq(ACCEPT)))
        .thenReturn(Optional.ofNullable(selfEntity));

    when(friendRepository.findByFriendUserEntityUserIdAndUserEntityUserIdAndStatus
        (anyLong(), anyLong(), eq(ACCEPT)))
        .thenReturn(Optional.ofNullable(otherEntity));


    when(friendRepository.save(any()))
        .thenReturn(selfDeleteEntity)
        .thenReturn(otherDeleteEntity);

    // when
    List<DeleteResponse> result = friendService.deleteFriend(request);

    // Then
    assertEquals(selfDeleteEntity.getFriendId(), result.get(0).getFriendId());
    assertEquals(selfDeleteEntity.getStatus(), result.get(0).getStatus());
    assertEquals(selfDeleteEntity.getUserEntity().getNickName(),
        result.get(0).getNickName());
    assertEquals(selfDeleteEntity.getFriendUserEntity().getNickName(),
        result.get(0).getFriendNickName());

    assertEquals(otherDeleteEntity.getFriendId(), result.get(1).getFriendId());
    assertEquals(otherDeleteEntity.getStatus(), result.get(1).getStatus());
    assertEquals(otherDeleteEntity.getUserEntity().getNickName(),
        result.get(1).getNickName());
    assertEquals(otherDeleteEntity.getFriendUserEntity().getNickName(),
        result.get(1).getFriendNickName());

  }

  @Test
  @DisplayName("친구 검색 성공")
  void searchNickName_success() {
    // Given
    String nickName = "test";
    Pageable pageable = PageRequest.of(0, 4);

    UserEntity otherEntity = UserEntity.builder()
        .userId(3L)
        .birthday(LocalDate.of(1990, 1, 1))
        .gender(GenderType.MALE)
        .nickName("test2")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .build();

    FriendEntity friendEntity1 = FriendEntity.builder()
        .friendId(1L)
        .status(ACCEPT)
        .userEntity(userEntity)
        .friendUserEntity(friendUserEntity1)
        .build();

    FriendEntity friendEntity2 = FriendEntity.builder()
        .friendId(2L)
        .status(ACCEPT)
        .userEntity(friendUserEntity1)
        .friendUserEntity(userEntity)
        .build();

    ListResponse listResponse1 = ListResponse.builder()
        .userId(2L)
        .birthday(LocalDate.of(1990, 1, 1))
        .nickName("test1")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .friendId(1L)
        .build();

    ListResponse listResponse2 = ListResponse.builder()
        .userId(3L)
        .birthday(LocalDate.of(1990, 1, 1))
        .nickName("test2")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .friendId(null)
        .build();

    List<ListResponse> listResponseList =
        List.of(listResponse1, listResponse2);

    Page<ListResponse> searchResponsePage =
        new PageImpl<>(listResponseList, pageable, 2);

    getCurrentUser();

    when(friendCustomRepository.findBySearchFriendList
        (1L, nickName, pageable))
        .thenReturn(searchResponsePage);


    // when
    Page<ListResponse> result = friendService.searchNickName(nickName, pageable);
    List<ListResponse> responseList = result.getContent();

    // Then
    assertEquals(listResponse1.getUserId(), responseList.get(0).getUserId());
    assertEquals(listResponse1.getBirthday(), responseList.get(0).getBirthday());
    assertEquals(listResponse1.getNickName(), responseList.get(0).getNickName());
    assertEquals(listResponse1.getPlayStyle(), responseList.get(0).getPlayStyle());
    assertEquals(listResponse1.getAbility(), responseList.get(0).getAbility());
    assertEquals(listResponse1.getFriendId(), responseList.get(0).getFriendId());

    assertEquals(listResponse2.getFriendId(), responseList.get(1).getFriendId());
    assertEquals(listResponse2.getBirthday(),
        responseList.get(1).getBirthday());
    assertEquals(listResponse2.getNickName(),
        responseList.get(1).getNickName());
    assertEquals(listResponse2.getPlayStyle(),
        responseList.get(1).getPlayStyle());
    assertEquals(listResponse2.getAbility(),
        responseList.get(1).getAbility());
    assertEquals(listResponse2.getFriendId(),
        responseList.get(1).getFriendId());
  }


  @Test
  @DisplayName("친구 리스트 조회 성공")
  void getMyFriends_success() {
    // Given
    Pageable pageable = PageRequest.of(0, 1);

    FriendEntity friendEntity1 = FriendEntity.builder()
        .friendId(1L)
        .status(ACCEPT)
        .userEntity(userEntity)
        .friendUserEntity(friendUserEntity1)
        .build();

    FriendEntity friendEntity2 = FriendEntity.builder()
        .friendId(2L)
        .status(ACCEPT)
        .userEntity(friendUserEntity1)
        .friendUserEntity(userEntity)
        .build();

    ListResponse listResponse1 = ListResponse.builder()
        .userId(2L)
        .birthday(LocalDate.of(1990, 1, 1))
        .nickName("test1")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .friendId(1L)
        .build();

    List<FriendEntity> friendEntityList =
        List.of(friendEntity1);

    Page<FriendEntity> searchResponsePage =
        new PageImpl<>(friendEntityList, pageable, 1);

    getCurrentUser();

    when(friendRepository.findByUserEntityUserId
        (1L, pageable))
        .thenReturn(searchResponsePage);


    // when
    List<ListResponse> result = friendService.getMyFriends(pageable);

    // Then
    assertEquals(listResponse1.getUserId(), result.get(0).getUserId());
    assertEquals(listResponse1.getBirthday(), result.get(0).getBirthday());
    assertEquals(listResponse1.getNickName(), result.get(0).getNickName());
    assertEquals(listResponse1.getPlayStyle(), result.get(0).getPlayStyle());
    assertEquals(listResponse1.getAbility(), result.get(0).getAbility());
    assertEquals(listResponse1.getFriendId(), result.get(0).getFriendId());
  }

  @Test
  @DisplayName("내가 친구 요청 받은 리스트 조회 성공")
  void getRequestFriendList_success() {
    // Given

    FriendEntity friendEntity1 = FriendEntity.builder()
        .friendId(1L)
        .status(ACCEPT)
        .userEntity(friendUserEntity1)
        .friendUserEntity(userEntity)
        .build();

    FriendEntity friendEntity2 = FriendEntity.builder()
        .friendId(2L)
        .status(ACCEPT)
        .userEntity(friendUserEntity2)
        .friendUserEntity(userEntity)
        .build();

    RequestListResponse RequestListResponse1 = RequestListResponse.builder()
        .userId(2L)
        .birthday(LocalDate.of(1990, 1, 1))
        .nickName("test1")
        .playStyle(PlayStyleType.AGGRESSIVE)
        .ability(AbilityType.SHOOT)
        .mannerPoint(null)
        .friendId(1L)
        .build();

    RequestListResponse RequestListResponse2 = RequestListResponse.builder()
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

    getCurrentUser();

    when(friendRepository.findByStatusAndFriendUserEntityUserId
        (eq(FriendStatus.APPLY), anyLong()))
        .thenReturn(friendEntityList);


    // when
    List<RequestListResponse> result = friendService.getRequestFriendList();

    // Then
    assertEquals(RequestListResponse1.getUserId(), result.get(0).getUserId());
    assertEquals(RequestListResponse1.getBirthday(), result.get(0).getBirthday());
    assertEquals(RequestListResponse1.getNickName(), result.get(0).getNickName());
    assertEquals(RequestListResponse1.getPlayStyle(), result.get(0).getPlayStyle());
    assertEquals(RequestListResponse1.getAbility(), result.get(0).getAbility());
    assertEquals(RequestListResponse1.getFriendId(), result.get(0).getFriendId());
    assertEquals(RequestListResponse1.getMannerPoint(), result.get(0).getMannerPoint());

    assertEquals(RequestListResponse2.getUserId(), result.get(1).getUserId());
    assertEquals(RequestListResponse2.getBirthday(),
        result.get(1).getBirthday());
    assertEquals(RequestListResponse2.getNickName(),
        result.get(1).getNickName());
    assertEquals(RequestListResponse2.getPlayStyle(),
        result.get(1).getPlayStyle());
    assertEquals(RequestListResponse2.getAbility(), result.get(1).getAbility());
    assertEquals(RequestListResponse2.getMannerPoint(),
        result.get(1).getMannerPoint());
  }

  private void getCurrentUser() {
    when(jwtTokenExtract.currentUser()).thenReturn(userEntity);

    when(userRepository.findById(1L)).thenReturn(
        Optional.ofNullable(userEntity));
  }

}