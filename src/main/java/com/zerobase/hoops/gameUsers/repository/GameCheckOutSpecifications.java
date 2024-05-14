package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.entity.GameEntity;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class GameCheckOutSpecifications {

  public static Specification<GameEntity> withCityName(CityName cityName) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("cityName"), cityName);
  }

  public static Specification<GameEntity> withFieldStatus(
      FieldStatus fieldStatus) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("fieldStatus"), fieldStatus);
  }

  public static Specification<GameEntity> withGender(Gender gender) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("gender"), gender);
  }

  public static Specification<GameEntity> withMatchFormat(
      MatchFormat matchFormat) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("matchFormat"), matchFormat);
  }

  public static Specification<GameEntity> startDate(
      LocalDate date) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.get("startDateTime").as(LocalDate.class), date);
  }

  public static Specification<GameEntity> notDeleted() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.isNull(root.get("deletedDateTime"));
  }
}
