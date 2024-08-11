package com.zerobase.hoops.gameUsers.repository;

import com.zerobase.hoops.document.GameDocument;
import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface GameCustomRepository {

  List<GameDocument> findAllGameDocuments(
      LocalDate localDate, CityName cityName, FieldStatus fieldStatus, Gender gender, MatchFormat matchFormat);

}
