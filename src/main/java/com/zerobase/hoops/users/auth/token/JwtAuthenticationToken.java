package com.zerobase.hoops.users.auth.token;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private String token;
  private Object principal;
  private Object credentials;

  public JwtAuthenticationToken(
      Collection<? extends GrantedAuthority> authorities,
      Object principal, Object credentials) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    this.setAuthenticated(true);
  }

  public JwtAuthenticationToken(String token) {
    super(null);
    this.token = token;
    this.setAuthenticated(false);
  }
  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return null;
  }
}
