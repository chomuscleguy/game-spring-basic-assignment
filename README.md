## 필수 기능 구현

**Lv1. 설정 파일 작성 : Docker MySQL 연결**
- [x] Docker로 MySQL을 실행하고, 프로젝트에 환경 변수 설정 파일을 세팅합니다.

<details>
<summary><b>[자세히] </b></summary>

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

**Lv2. 의존성 주입(DI)**
- [x]  스프링을 실행 후 로그를 읽고, 어떤 클래스가 빈으로 등록되지 않았는지 찾아 고칩니다.

<details>
<summary><b>[자세히]</b></summary>

<br>

```문제 상황 (에러 로그 분석)
my-app | ***************************
my-app | APPLICATION FAILED TO START
my-app | ***************************
my-app |
my-app | Description:
my-app |
my-app | Parameter 0 of constructor in com.gamebasic.game.controller.GameController required a bean of type 'com.gamebasic.game.service.GameService' that could not be found.
my-app |
my-app |
my-app | Action:
my-app |
my-app | Consider defining a bean of type 'com.gamebasic.game.service.GameService' in your configuration.
my-app |
my-app exited with code 1 (restarting)
```
여기서 com.gamebasic.game.controller.GameController 클래스가 생성될 때, 첫 번째 생성자 파라미터(Parameter 0)인 com.gamebasic.game.service.GameService 객체(빈)를 주입받으려고 했으나 찾지 못해 애플리케이션 구동이 실패했다는 뜻입니다.

즉, GameController는 준비되었는데 그 안에 넣어줄 GameService가 스프링 빈으로 등록되지 않아서 발생한 에러입니다.

```java
@Service //추가
@RequiredArgsConstructor
public class GameService {
}
```

@Service 어노테이션을 추가하여 스프링 컨테이너가 해당 클래스를 빈(Bean)으로 자동 등록하도록 유도함으로써 의존성 주입 문제를 해결했습니다.
기술적으로 @Component와 @Service의 동작은 동일하지만, 컨트롤러 계층에 @RestController를 사용하는 것과의 아키텍처 일관성을 유지하고 가독성을 높이기 위해 @Service를 채택했습니다.

</details>

- [x]  확인: 서버가 뜨고 `http://localhost:8080/`에서 아래 게임 타이틀이 열립니다. MySQL에 테이블이 생긴 것도 확인해 보세요.

<details>
<summary><b>[자세히]</b></summary>

<br>

```Bash
##docker의 mySQL에 접속
docker exec -it assignment-mysql mysql -u root -p
```

```Bash
##assignment_db 사용
use assignment_db;
```

```Bash
##assignment_db 테이블 보기
show tables;
```

```Bash
+-------------------------+
| Tables_in_assignment_db |
+-------------------------+
| games                   |
| run_cards               |
+-------------------------+
```

</details>

**Lv3. RESTful API: 게임 목록 조회**
- [x] GET http://localhost:8080/games를 호출하면 지금은 405가 나옵니다. 200이 나오게 고쳐야 합니다.
- [x] 확인: 요청이 200과 빈 목록([])을 반환하고, 게임 타이틀이 에러 없이 열립니다.

<details>
<summary><b>[자세히] </b></summary>

<br>

![레벨 3 에러 해결 화면](./img/lv3_img.png)

</details>

**Lv4. @Transactional**
게임에서는 타이틀 화면까지는 정상이지만, "게임 시작"을 눌러 이름을 입력하고 "새 게임" 버튼을 누르는 순간 저장 단계에서 에러가 납니다.
- [x]  "새 게임"을 눌러 에러를 확인하고 수정합니다.
- [x]  확인: "새 게임"을 누르면 에러 메시지가 아래 오른쪽처럼 `서버 응답이 API 명세와 다릅니다 (deck[0].id)`로 바뀝니다. 저장은 성공했지만 아직 비어 있는 응답 DTO 때문에 나는 메시지입니다.

<details>
<summary><b>[자세히] </b></summary>

<br>

```문제 상황(에러 코드)
my-app  | Hibernate: insert into games (current_floor,current_hp,phase,player_name,status) values (?,?,?,?,?)
my-app  | 2026-09-08T06:58:46.993Z  WARN 1 --- [nio-8080-exec-7] org.hibernate.orm.jdbc.error             : HHH000247: ErrorCode: 0, SQLState: S1009
my-app  | 2026-09-08T06:58:46.994Z  WARN 1 --- [nio-8080-exec-7] org.hibernate.orm.jdbc.error             : Connection is read-only. Queries leading to data modification are not allowed
my-app  | 2026-09-08T06:58:46.997Z ERROR 1 --- [nio-8080-exec-7] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: org.springframework.orm.jpa.JpaSystemException: could not execute statement [Connection is read-only. Queries leading to data modification are not allowed] [insert into games (current_floor,current_hp,phase,player_name,status) values (?,?,?,?,?)]] with root cause
my-app  | 
my-app  | java.sql.SQLException: Connection is read-only. Queries leading to data modification are not allowed
my-app  |       at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:121) ~[mysql-connector-j-9.7.0.jar!/:9.7.0]
my-app  |       at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:89) ~[mysql-connector-j-9.7.0.jar!/:9.7.0]
my-app  |       at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:81) ~[mysql-connector-j-9.7.0.jar!/:9.7.0]
my-app  |       at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:55) ~[mysql-connector-j-9.7.0.jar!/:9.7.0]
```
여기서 Connection is read-only. Queries leading to data modification are not allowed는 서비스 클래스나, 메서드에 붙어있는 @Transactional에 readOnly = true가 들어가 있어 발생하는 오류입니다.

```java
@Transactional() //readOnly = ture -> 공백 or readOnly = false로 수정
public GameDetailResponse createGame(CreateRequest request) {
}
```

![레벨 4 에러 해결 화면](./img/lv4_img.png)

</details>