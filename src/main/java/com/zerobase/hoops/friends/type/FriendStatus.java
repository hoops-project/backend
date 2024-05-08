package com.zerobase.hoops.friends.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendStatus {
  APPLY, ACCEPT, REJECT, CANCEL, DELETE;

}
