package com.zerobase.hoops.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "HOOPS API Document",
        description = "API Document",
        version = "v0.1",
        contact = @Contact(
            name = "HOOPS",
            email = "admin@hoops.com"
        )
    ),
    tags = {
        @Tag(name = "USER", description = "회원 기능"),
        @Tag(name = "AUTH", description = "인증/인가 기능"),
        @Tag(name = "OAUTH2", description = "소셜 로그인"),
        @Tag(name = "GAME-USER", description = "경기 유저 기능"),
        @Tag(name = "GAME-CREATOR", description = "경기 개설자 기능"),
        @Tag(name = "PARTICIPANT", description = "경기 참가 기능"),
        @Tag(name = "REPORT", description = "신고 기능"),
        @Tag(name = "FRIEND", description = "친구 기능"),
        @Tag(name = "INVITE", description = "초대 기능"),
        @Tag(name = "ALARM", description = "알람 기능"),
        @Tag(name = "MANAGER", description = "관리자 기능"),
    }
)
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    SecurityScheme securityScheme = new SecurityScheme()
        .type(SecurityScheme.Type.HTTP).scheme("Bearer").bearerFormat("JWT")
        .in(SecurityScheme.In.HEADER).name("Authorization");
    SecurityRequirement securityRequirement = new SecurityRequirement().addList(
        "bearerAuth");

    return new OpenAPI()
        .components(
            new Components().addSecuritySchemes("bearerAuth", securityScheme))
        .security(Arrays.asList(securityRequirement));
  }
}
