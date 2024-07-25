# 빌드 이미지로 OpenJDK 17 & Gradle 지정
FROM gradle:8.7.0-jdk17 AS build

# 소스코드를 복사할 작업 디렉토리 생성
WORKDIR /app

# 호스트 머신의 소스코드를 작업 디렉토리로 복사
COPY . /app

# gradlew에 실행 권한 부여 및 라인 엔딩 수정
RUN chmod +x ./gradlew
RUN sed -i 's/\r$//' ./gradlew

# Gradle 빌드를 실행하여 JAR 파일 생성
RUN ./gradlew clean build --no-daemon

# 런타임 이미지로 OpenJDK 17 slim-buster
FROM openjdk:17-slim-buster

# 시스템 패키지 업데이트 및 보안 업데이트 적용
RUN apt-get update && \
    apt-get upgrade -y --only-upgrade --no-install-recommends && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 비 루트 사용자 생성
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser

# 애플리케이션을 실행할 작업 디렉토리 생성
WORKDIR /app

# 빌드 이미지에서 생성된 JAR 파일을 런타임 이미지로 복사
COPY --from=build /app/build/libs/*.jar /app/hoops.jar

# logs 디렉토리 생성 및 권한 설정
RUN mkdir -p /app/logs && chown -R javauser:javauser /app

# 비 루트 사용자로 전환
USER javauser

EXPOSE 8080

# ENTRYPOINT와 CMD를 결합하여 사용
CMD ["java", "-jar", "/app/hoops.jar"]