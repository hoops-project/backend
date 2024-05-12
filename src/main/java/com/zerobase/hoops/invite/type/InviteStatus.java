package com.zerobase.hoops.invite.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InviteStatus {
  REQUEST,
  CANCEL,
  ACCEPT,
  REJECT;
}
