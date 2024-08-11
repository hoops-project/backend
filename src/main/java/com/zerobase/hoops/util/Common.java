package com.zerobase.hoops.util;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Common {

  public static OffsetDateTime getNowDateTime() {
    // 포맷 정의 (타임존 오프셋 포함)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    // 현재 시간을 OffsetDateTime으로 생성
    OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.ofHours(9)); // KST는 UTC+9

    // 문자열로 포맷
    String formattedDateTime = offsetDateTime.format(formatter);

    // 문자열을 OffsetDateTime으로 파싱
    return OffsetDateTime.parse(formattedDateTime, formatter);
  }

  public static OffsetDateTime getNowDateTime(Clock clock) {
    // 포맷 정의 (타임존 오프셋 포함)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    // Clock을 사용하여 ZonedDateTime을 생성합니다.
    ZonedDateTime zonedDateTime = ZonedDateTime.now(clock);

    // ZonedDateTime을 OffsetDateTime으로 변환합니다.
    OffsetDateTime offsetDateTime = zonedDateTime.withZoneSameInstant(
            ZoneOffset.ofHours(9))
        .toOffsetDateTime();

    // 문자열로 포맷
    String formattedDateTime = offsetDateTime.format(formatter);

    // 문자열을 OffsetDateTime으로 파싱
    return OffsetDateTime.parse(formattedDateTime, formatter);
  }

}
