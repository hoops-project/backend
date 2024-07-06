package com.zerobase.hoops.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "report")
public class ReportEntity {

  @Id
  @GeneratedValue
  private Long id;

  private String content;

  @ManyToOne
  private UserEntity user;

  @ManyToOne
  private UserEntity reportedUser;

  @CreatedDate
  private LocalDateTime createdDateTime;

  private LocalDateTime blackListStartDateTime;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReportEntity that = (ReportEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public void saveBlackListStartDateTime(LocalDateTime dateTIme){
    this.blackListStartDateTime = dateTIme;
  }
}
