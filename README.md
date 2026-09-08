## 필수 기능 구현

- [x] **Lv1. 설정 파일 작성 : Docker MySQL 연결**

<details>
<summary><b>[클릭] Docker 환경 구축 및 실행 절차 상세 보기</b></summary>

<br>

---

### 1. Dockerfile 구성
* Java 21 실행 환경 구축 및 실행 가능한 `.jar` 파일 등록

 [Dockerfile 바로가기](./Dockerfile)

---

### 2. 애플리케이션 아티팩트 빌드
* Docker 이미지 빌드 전, Gradle을 통해 실행 가능한 `.jar` 파일 생성
```bash
./gradlew clean build -x test
```
---
### 3. docker-compose.yml 구성
MySQL 데이터베이스와 Spring Boot 애플리케이션 컨테이너 묶음 관리

DB 데이터 영속성을 위한 볼륨 마운트 및 healthcheck 조건 적용

 [docker-compose.yml 바로가기](./docker-compose.yml)

---

### 4. 환경 변수 세팅 (.env)
DB 접속 정보 및 루트 비밀번호 등 보안 민감 정보 분리 (.gitignore 처리)

협업 및 평가용 환경 변수 스키마 제공

 [.env.example 바로가기](./.env.example)
 
---

### 5. Docker Compose 서비스 실행
설정된 환경 변수와 Compose 파일 기반으로 서비스 일괄 구동

```Bash
# 전체 컨테이너 빌드 및 백그라운드 실행
docker-compose up -d --build

# 실행 상태 확인
docker-compose ps
```

---

</details>