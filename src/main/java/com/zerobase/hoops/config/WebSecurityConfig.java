package com.zerobase.hoops.config;

import com.zerobase.hoops.security.JwtAuthenticationFilter;
import com.zerobase.hoops.security.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
@Slf4j
@Configurable
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtExceptionFilter jwtExceptionFilter;

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
            .frameOptions(FrameOptionsConfig::disable))
        .authorizeHttpRequests(request -> request
            .requestMatchers(
                "/api/user/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api/auth/login",
                "/api/oauth2/login/kakao",
                "/api/oauth2/kakao",
                "/api/game-user/**",
                "/h2-console/**",
                "/api/game-creator/game/detail").permitAll()
            .requestMatchers("/ws/**").permitAll()
            .requestMatchers("/api/chat/create")
            .hasAnyRole("USER")
            .requestMatchers("/api/auth/**")
            .hasAnyRole("USER", "CREATOR", "ADMIN")
            .requestMatchers("/api/oauth2/logout/kakao")
            .hasAnyRole("USER")
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter,
            JwtAuthenticationFilter.class);
    return httpSecurity.build();
  }

  @Bean
  protected CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    // 웹소켓 로컬 테스트
    corsConfiguration.addAllowedOrigin("http://127.0.0.1:5001");
    //
    corsConfiguration.addAllowedOrigin("http://localhost:5173");
    corsConfiguration.addAllowedOrigin(
        "https://hoops-frontend-jet.vercel.app");
    corsConfiguration.addAllowedOrigin("https://hoops.services");
    corsConfiguration.addAllowedMethod("*");
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.addExposedHeader("Authorization");
    corsConfiguration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);

    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
