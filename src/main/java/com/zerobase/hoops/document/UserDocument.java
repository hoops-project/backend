package com.zerobase.hoops.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zerobase.hoops.users.dto.EditDto;
import com.zerobase.hoops.users.type.AbilityType;
import com.zerobase.hoops.users.type.GenderType;
import com.zerobase.hoops.users.type.PlayStyleType;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "index_users")
public class UserDocument implements UserDetails {

  @Id
  private String id;

  @Field(type = FieldType.Text)
  private String loginId;

  @Field(type = FieldType.Text)
  private String password;

  @Field(type = FieldType.Text)
  private String email;

  @Field(type = FieldType.Text)
  private String name;

  @Field(type = FieldType.Date, format = DateFormat.date, pattern = "yyyyMMdd")
  private LocalDate birthday;

  @Field(type = FieldType.Keyword)
  private GenderType gender;

  @Field(type = FieldType.Text)
  private String nickName;

  @Field(type = FieldType.Keyword)
  private PlayStyleType playStyle;

  @Field(type = FieldType.Keyword)
  private AbilityType ability;

  @Field(type = FieldType.Integer)
  private int totalRatings;

  @Field(type = FieldType.Integer)
  private int totalRatingsCount;

  @Field(type = FieldType.Double)
  private double doubleAverageRating;

  @Field(type = FieldType.Text)
  private String stringAverageRating;

  @Field(type = FieldType.Keyword)
  private List<String> roles;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime createdDateTime;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime deletedDateTime;

  @Field(type = FieldType.Boolean)
  private boolean emailAuth;

  public void confirm() {
    this.emailAuth = true;
  }

  public void saveMannerPoint(int point) {
    this.totalRatings += point;
    this.totalRatingsCount++;
    this.doubleAverageRating = (double) this.totalRatings / this.totalRatingsCount;
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
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDocument that = (UserDocument) o;
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
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
