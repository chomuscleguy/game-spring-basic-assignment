# 1단계: 빌드 스테이지 (도커 안에서 그레이들 빌드 수행)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

# 프로젝트 파일 전체 복사
COPY . .

# 윈도우/리눅스 개행문자 충돌 방지 및 gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# bootJar 실행 (테스트 스킵으로 빌드 속도 향상)
RUN ./gradlew bootJar -x test --no-daemon

# 2단계: 실행 스테이지
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일만 쏙 가져오기
COPY --from=builder /build/build/libs/*.jar /app/myapp.jar

ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]