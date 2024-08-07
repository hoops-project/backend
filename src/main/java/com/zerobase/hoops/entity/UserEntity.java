package com.zerobase.hoops.entity;

import com.zerobase.hoops.users.dto.EditDto;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @Column(nullable = false)
  private String loginId;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private LocalDate birthday;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GenderType gender;

  @Column(nullable = false)
  private String nickName;

  @Enumerated(EnumType.STRING)
  private PlayStyleType playStyle;

  @Enumerated(EnumType.STRING)
  private AbilityType ability;

  @Builder.Default
  @Column(name = "total_ratings")
  private int totalRatings = 0;

  @Builder.Default
  @Column(name = "total_ratings_count")
  private int totalRatingsCount = 0;

  @Builder.Default
  @Column(name = "double_average_rating")
  private double doubleAverageRating = 0.0;
  private String stringAverageRating;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  private List<String> roles;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdDateTime;

  private LocalDateTime deletedDateTime;

  @ColumnDefault("false")
  @Column(nullable = false)
  private boolean emailAuth;

  public void confirm() {
    this.emailAuth = true;
  }

  public void saveMannerPoint(int point) {
    this.totalRatings += point;
    this.totalRatingsCount++;
    this.doubleAverageRating =
        (double) this.totalRatings / this.totalRatingsCount;
    DecimalFormat df = new DecimalFormat("#.#");
    this.stringAverageRating = df.format(doubleAverageRating);
  }

  public void passwordEdit(String password) {
    if (!password.isEmpty()) {
      this.password = password;
    }
  }

  public void edit(EditDto.Request request) {
    if (request.getName() != null) {
      this.name = request.getName();
    }
    if (request.getNickName() != null) {
      this.nickName = request.getNickName();
    }
    if (request.getBirthday() != null) {
      this.birthday = request.getBirthday();
    }
    if (request.getGender() != null) {
      this.gender = GenderType.valueOf(request.getGender());
    }
    if (request.getPlayStyle() != null) {
      this.playStyle = PlayStyleType.valueOf(request.getPlayStyle());
    }
    if (request.getAbility() != null) {
      this.ability = AbilityType.valueOf(request.getAbility());
    }
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserEntity that = (UserEntity) o;
    return Objects.equals(loginId, that.loginId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(loginId);
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}
