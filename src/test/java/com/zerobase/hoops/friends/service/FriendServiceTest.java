package com.zerobase.hoops.friends.service;

import static com.zerobase.hoops.friends.type.FriendStatus.ACCEPT;
import static com.zerobase.hoops.friends.type.FriendStatus.APPLY;
import static com.zerobase.hoops.friends.type.FriendStatus.CANCEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.hoops.entity.FriendEntity;
import com.zerobase.hoops.entity.UserEntity;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyRequest;
import com.zerobase.hoops.friends.dto.FriendDto.ApplyResponse;
import com.zerobase.hoops.friends.dto.FriendDto.CancelRequest;
import com.zerobase.hoops.friends.dto.FriendDto.CancelResponse;
import com.zerobase.hoops.friends.repository.FriendRepository;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

  @InjectMocks
  private FriendService friendService;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  @Mock
  private UserRepository userRepository;

  @Mock
  private FriendRepository friendRepository;

  private UserEntity applyUserEntity;
  private UserEntity friendUserEntity;

  @BeforeEach
  void setUp() {
    applyUserEntity = UserEntity.builder()
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
    friendUserEntity = UserEntity.builder()
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
    when(friendRepository.findByFriendIdAndStatusIn
            (anyLong(), any()))
        .thenReturn(0);
    
    // 친구 목록에 10명이 있다고 가정
    when(friendRepository.findByFriendUserEntityUserIdAndStatus
        (anyLong(), eq(ACCEPT)))
        .thenReturn(10);


    when(userRepository.findById(2L)).thenReturn(Optional.of(friendUserEntity));

    FriendEntity friendEntity = ApplyRequest.toEntity(
        applyUserEntity, friendUserEntity
    );

    when(friendRepository.save(any())).thenAnswer(invocation -> {
      FriendEntity savedFriendEntity = invocation.getArgument(0);
      savedFriendEntity.setFriendId(1L); // friendId 동적 할당
      return savedFriendEntity;
    });

    // when
    ApplyResponse response = friendService.applyFriend(request);

    // Then
    assertEquals(1L, response.getFriendId());
    assertEquals(friendEntity.getStatus(),
        response.getStatus());
    assertEquals(friendEntity.getApplyUserEntity().getNickName(),
        response.getApplyNickName());
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

    FriendEntity friendEntity = FriendEntity.builder()
        .friendId(1L)
        .status(APPLY)
        .createdDateTime(LocalDateTime.of(2024, 5, 25, 0, 0, 0))
        .applyUserEntity(applyUserEntity)
        .friendUserEntity(friendUserEntity)
        .build();

    FriendEntity cancelEntity = FriendEntity.builder()
        .friendId(1L)
        .status(CANCEL)
        .createdDateTime(LocalDateTime.of(2024, 5, 25, 0, 0, 0))
        .canceledDateTime(LocalDateTime.of(2024, 5, 25, 8, 0, 0))
        .applyUserEntity(applyUserEntity)
        .friendUserEntity(friendUserEntity)
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
    assertEquals(cancelEntity.getApplyUserEntity().getNickName(),
        response.getApplyNickName());
    assertEquals(cancelEntity.getFriendUserEntity().getNickName(),
        response.getFriendNickName());

  }

  private void getCurrentUser() {
    when(jwtTokenExtract.currentUser()).thenReturn(applyUserEntity);

    when(userRepository.findById(1L)).thenReturn(
        Optional.ofNullable(applyUserEntity));
  }

}