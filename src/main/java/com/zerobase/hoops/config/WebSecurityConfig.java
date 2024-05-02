package com.zerobase.hoops.config;

import com.zerobase.hoops.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configurable
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  protected SecurityFilterChain configure(HttpSecurity httpSecurity)
      throws Exception {

    httpSecurity
        .cors(cors -> cors
            .configurationSource(corsConfigurationSource())
        )
        .formLogin(AbstractHttpConfigurer::disable)
        .csrf(CsrfConfigurer::disable)
        .httpBasic(HttpBasicConfigurer::disable)
        .sessionManagement(sessionManagement -> sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .headers(header -> header
            .frameOptions(options -> options.disable()))
        .authorizeHttpRequests(request -> request
            .requestMatchers("/", "/api/user/**",
                "/swagger-ui/**", "/v3/api-docs/**",
                //로그인 개발되면 해당 부분 삭제
                "/report/user",
                //--------------------
                "/h2-console/**").permitAll()
            .requestMatchers("/api/auth/**")
            .hasAnyRole("USER", "CREATOR", "ADMIN")
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/game-creator/game/create")
            .hasRole("USER")
            .requestMatchers("/api/game-creator/game/update")
            .hasRole("CREATOR")
            .requestMatchers("/api/game-creator/game/delete")
            .hasAnyRole("CREATOR", "ADMIN")
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
  }

  @Bean
  protected CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.addAllowedOrigin("http://localhost:3000");
    corsConfiguration.addAllowedMethod("*");
    corsConfiguration.addAllowedHeader("*");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);

    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    ProviderManager auth =
        (ProviderManager) authenticationConfiguration.getAuthenticationManager();
    return auth;
  }
}
