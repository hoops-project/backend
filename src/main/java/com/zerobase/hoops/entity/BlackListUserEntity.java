package com.zerobase.hoops.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
@Table(name = "blacklist_user")
public class BlackListUserEntity {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private UserEntity blackUser;

  @CreatedDate
  private LocalDate startDate;

  private LocalDate endDate;

  public void unLockBlackList() {
    this.endDate = LocalDate.now().minusDays(1);
  }
}
