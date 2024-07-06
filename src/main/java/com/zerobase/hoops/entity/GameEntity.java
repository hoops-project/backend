package com.zerobase.hoops.entity;

import com.zerobase.hoops.gameCreator.type.CityName;
import com.zerobase.hoops.gameCreator.type.FieldStatus;
import com.zerobase.hoops.gameCreator.type.Gender;
import com.zerobase.hoops.gameCreator.type.MatchFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "game")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class GameEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private Long headCount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FieldStatus fieldStatus;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Gender gender;

  @Column(nullable = false)
  private LocalDateTime startDateTime;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdDateTime;

  private LocalDateTime deletedDateTime;

  @Column(nullable = false)
  private Boolean inviteYn;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String placeName;

  @Column(nullable = false)
  private Double latitude;

  @Column(nullable = false)
  private Double longitude;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CityName cityName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MatchFormat matchFormat;

  @ManyToOne
  @JoinColumn(nullable = false)
  private UserEntity user;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GameEntity that = (GameEntity) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(title, that.title) &&
        Objects.equals(content, that.content) &&
        Objects.equals(headCount, that.headCount) &&
        Objects.equals(fieldStatus, that.fieldStatus) &&
        Objects.equals(gender, that.gender) &&
        Objects.equals(startDateTime, that.startDateTime) &&
        Objects.equals(createdDateTime, that.createdDateTime) &&
        Objects.equals(deletedDateTime, that.deletedDateTime) &&
        Objects.equals(inviteYn, that.inviteYn) &&
        Objects.equals(address, that.address) &&
        Objects.equals(placeName, that.placeName) &&
        Objects.equals(latitude, that.latitude) &&
        Objects.equals(longitude, that.longitude) &&
        Objects.equals(cityName, that.cityName) &&
        Objects.equals(matchFormat, that.matchFormat) &&
        Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, headCount, fieldStatus, gender,
        startDateTime, createdDateTime, deletedDateTime, inviteYn, address,
        placeName, latitude, longitude, cityName, matchFormat, user);
  }

}
