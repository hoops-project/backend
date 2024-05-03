package com.zerobase.hoops.gameCreator.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CityName {
  SEOUL("서울"),
  GYEONGGI("경기"),
  INCHEON("인천"),
  GANGWON("강원특별자치도"),
  DAEJEON("대전"),
  SEJONG("세종특별자치시"),
  CHUNGNAM("충남"),
  CHUNGBUK("충북"),
  DAEGU("대구"),
  GYEONGBUK("경북"),
  BUSAN("부산"),
  ULSAN("울산"),
  GYEONGNAM("경남"),
  GWANGJU("광주"),
  JEONNAM("전남"),
  JEONBUK("전북특별자치도"),
  JEJU("제주특별자치도");

  private String hangeulName;

  public static CityName getCityName(String address) {
    String[] parts = address.split(" ");
    String firstWord = parts[0];

    for (CityName cityName : CityName.values()) {
      if (cityName.getHangeulName().equals(firstWord)) {
        return cityName;
      }
    }

    return null; // 일치하는 도시 이름을 찾지 못한 경우
  }
}
