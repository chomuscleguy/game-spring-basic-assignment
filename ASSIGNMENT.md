## 1️⃣ **필수 기능 가이드**

### 과제 시작 전, 꼭 읽어보기

### 개발 전, 공통 조건

<aside>

- Controller, Service, Repository를 분리하여 3 Layer Architecture로 개발해야 합니다.
- 엔티티 연관관계는 단방향으로만 사용합니다. cascade, orphanRemoval, 양방향 컬렉션은 사용하지 않으며, 자식(RunCard)의 조회·저장·삭제는 Repository를 통해 명시적으로 처리합니다.
- CRUD 필수 기능은 Docker로 실행한 MySQL과 JPA를 사용해서 개발해야 합니다.
- 완성된 게임 화면(프론트엔드)이 프로젝트에 포함되어 있습니다. 설정 파일을 만들고 `src/main/java`의 `TODO`를 모두 채우면 `./gradlew bootRun` 후 `http://localhost:8080/`에서 게임이 실제로 동작합니다.
- API 경로, JSON 필드, enum 값은 API 명세와 정확히 일치해야 하며, 성공 상태 코드는 2xx이면 됩니다.
- API 명세: [https://f-api.github.io/game-spring-api-docs/basic/api-docs.html](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html). 각 단계의 **API 명세 →** 링크는 해당 위치로 바로 이동합니다.
- Entity 완성본, 게임 생성의 서비스·컨트롤러 코드, 진행 저장의 서비스 메서드, 전역 예외 처리의 뼈대는 미리 작성되어 제공됩니다. 비어 있는 DTO와 나머지 API는 API 명세를 읽고 직접 완성합니다. 뼈대에 있는 기존 클래스 이름과 패키지는 바꾸지 않습니다.
</aside>

- **참고**
  - 완성된 게임: https://nhahan.github.io/crimson-citadel/
    - 서버 없이 브라우저 저장소로 동작하는 정적 배포판이라 필수 기능만 보입니다. 도전 기능(저장 시간, 카드 수, 랭킹)은 여러분의 서버로 실행한 게임에서만 나타납니다.
  - 에셋 갤러리:  [**[Asset Gallery]**](https://nhahan.github.io/crimson-citadel/#assets)

---

### Lv 1. 설정 파일 작성: Docker MySQL 연결 `필수`

뼈대를 그대로 실행하면 서버가 뜨지 않습니다. 실패 원인을 하나씩 찾아 고칩니다.

- [ ]  `./gradlew bootRun`을 실행하고, 실패 로그에서 원인이 된 에러 메시지를 찾아 읽습니다.
- [ ]  Docker로 MySQL을 실행하고, `src/main/resources/application.properties`를 새로 만들어 datasource 설정(`url`, `driver-class-name`, `username`, `password`)을 컨테이너에 맞게 적습니다.
- [ ]  `spring.jpa.hibernate.ddl-auto`가 값에 따라 어떻게 동작하는지 알아보고, 재시작해도 저장이 유지되는 값을 골라 추가합니다.
- [ ]  확인: 다시 실행했을 때 데이터베이스 에러가 사라지고, 다른 종류의 에러 메시지가 나타나면 성공입니다.

### Lv 2. 빈 등록 고치기: 의존성 주입 `필수`

- [ ]  실패 로그의 `required a bean of type ...` 메시지를 읽고, 어떤 클래스가 빈으로 등록되지 않았는지 찾습니다.
- [ ]  스프링이 클래스를 빈으로 등록하는 방법과 생성자 주입의 동작을 학습하고, 원인이 된 클래스를 고칩니다.
- [ ]  확인: 서버가 뜨고 `http://localhost:8080/`에서 아래 게임 타이틀이 열립니다. MySQL에 테이블이 생긴 것도 확인해 보세요.

  ![게임 타이틀 화면](docs/images/lv2-title.png)

### Lv 3. RESTful 경로 맞추기: 게임 목록 API `필수`

**API 명세 → [게임 목록 조회 `GET /games`](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html#tag/게임/operation/getGames)**

목록 API는 뼈대에 이미 작성되어 있습니다. 그런데 게임은 목록을 불러오지 못합니다. REST에서는 자원을 복수형 명사 경로(`/games`)로 나타내고, 같은 경로에 HTTP 메서드(GET·POST)로 동작을 구분합니다. 명세는 이 규칙을 따르고 있습니다.

- [ ]  Postman으로 명세대로 `GET http://localhost:8080/games`를 호출하면 405(Method Not Allowed)가 옵니다. `/games` 경로 자체는 있지만 GET 매핑이 없다는 뜻입니다. 게임도 이 주소를 호출하기 때문에 목록을 불러오지 못하는 것입니다. 컨트롤러의 목록 메서드 매핑을 명세의 경로와 한 글자씩 대조해 원인을 찾아 고칩니다.
- [ ]  확인: 같은 요청이 `200`과 빈 목록(`[]`)을 반환하고, 게임 타이틀이 에러 없이 열립니다.

### Lv 4. @Transactional 버그 고치기 `필수`

제공된 생성 코드에는 트랜잭션 설정 버그가 하나 있습니다. 게임에서는 타이틀 화면까지는 정상이지만, "게임 시작"을 눌러 이름을 입력하고 "새 게임" 버튼을 누르는 순간 저장 단계에서 500이 나고 아래 왼쪽처럼 에러가 표시됩니다.

- [ ]  "새 게임"을 눌러 에러를 재현하고, 서버 로그에서 `Connection is read-only ...` 에러를 찾습니다.
  - [ ]  스택트레이스에서 `com.gamebasic` 패키지의 프레임을 찾으면 문제가 일어난 메서드가 나옵니다. 그 메서드의 선언부를 에러 문구와 비교해 보세요.
- [ ]  확인: "새 게임"을 누르면 에러 메시지가 아래 오른쪽처럼 `서버 응답이 API 명세와 다릅니다 (deck[0].id)`로 바뀝니다. 저장은 성공했지만 아직 비어 있는 응답 DTO 때문에 나는 메시지입니다.

  ![트랜잭션 버그 수정 전후의 에러 메시지](docs/images/lv4.png)

### Lv 5. 요청 검증과 응답 DTO: 게임 생성 `필수`

**API 명세 → [게임 생성 `POST /games`](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html#tag/게임/operation/createGame)**

- [ ]  `RunCardRequest`와 `CardResponse` DTO 클래스를 API 명세에 맞게 수정하세요.
- [ ]  확인: "새 게임"을 누르면 아래 오른쪽처럼 시작 보상 화면까지 열립니다. 보상을 고르면 에러가 나는 것은 아직 진행 저장 API가 없어서이며 정상입니다.

  ![DTO 완성 전후의 새 게임 화면](docs/images/lv5.png)

### Lv 6. 보상 카드 선택과 진행 저장 `필수`

**API 명세 → [진행과 전체 덱 저장 `PUT /games/{gameId}/progress`](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html#tag/게임/operation/updateProgress)**

시작 보상 화면에서 카드를 고르는 순간 게임이 멈춥니다. 게임은 고른 카드가 더해진 덱 전체(시작 덱 9장 + 1장)를 이 API로 보내는데, 서버에 그 API가 없기 때문입니다. 전투가 끝나거나 층을 옮길 때도 같은 API를 호출합니다.

- [ ]  진행 저장 API를 구현하세요. 요청 DTO(`ProgressRequest`)와 서비스 메서드 `updateProgress`는 제공되므로, 컨트롤러의 주석 처리된 메서드를 완성합니다.
- [ ]  확인: 보상 선택부터 전투, 층 이동까지 게임이 에러 없이 플레이됩니다.
  - [ ]  브라우저를 새로 고치면 여정이 보이지 않는 것이 정상입니다. 목록 API가 아직 빈 목록(`[]`)만 반환하기 때문입니다.

  ![진행 저장 적용 전후의 게임 화면](docs/images/lv6.png)

### Lv 7. 목록·상세 조회: 저장된 여정 이어하기 `필수`

**API 명세 → [게임 목록 조회 `GET /games`](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html#tag/게임/operation/getGames), [게임 상세 조회 `GET /games/{gameId}`](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html#tag/게임/operation/getGame)**

- [ ]  Spring Data JPA가 커스텀 쿼리 메서드로 정렬 조회를 만들어 주는 규칙(OrderBy, Asc, Desc)을 직접 검색해서 공부하시고 문제를 풀어주세요.
- [ ]  아래의 코드를 이용하여 게임 목록 조회 API를 구현하세요. 응답 DTO `GameSummaryResponse`는 API 명세를 보고 새로 만듭니다. 게임 목록은 `Game`의 `id` 기준 내림차순입니다.

    ```java
    @GetMapping("/games")
    public ResponseEntity<List<GameSummaryResponse>> getGames() {
        return ResponseEntity.ok(gameService.getGames());
    }
    ```

- [ ]  아래의 코드를 이용하여 게임 상세 조회 API를 구현하세요.

    ```java
    @GetMapping("/games/{gameId}")
    public ResponseEntity<GameDetailResponse> getGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }
    ```

    - [ ]  `GameDetailResponse`의 `List<CardResponse> deck`은 `RunCard`의 `id` 기준 오름차순입니다.

- [ ]  확인: 새로 고쳐도 아래처럼 "저장된 여정"에 게임이 남아 있고, 선택하면 저장된 HP·층·덱이 그대로 이어집니다.

  ![저장된 여정 목록](docs/images/lv7-list.png)

### Lv 8. 변경 감지로 이름 수정, 자식부터 삭제 `필수`

**API 명세 → [플레이어 이름 변경 `PATCH /games/{gameId}`](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html#tag/게임/operation/renameGame), [게임 삭제 `DELETE /games/{gameId}`](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html#tag/게임/operation/deleteGame)**

- [ ]  아래의 코드를 이용하여 이름 변경 API를 구현하세요. 요청 DTO(`RenameRequest`)는 새로 만들고, Entity의 `rename()`메서드를 활용하여 더티 체킹 방식으로 업데이트합니다.

    ```java
    @PatchMapping("/games/{gameId}")
    public ResponseEntity<Void> renameGame(
        @PathVariable Long gameId,
        @Valid @RequestBody RenameRequest request
    ) {
        gameService.renameGame(gameId, request);
        return ResponseEntity.noContent().build();
    }
    ```

- [ ]  아래의 코드를 이용하여 삭제 API를 구현하세요. `gameId` 조건에 맞는 `Game`과 `RunCard`가 모두 삭제되어야합니다.

    ```java
    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }
    ```

- [ ]  확인: 저장된 여정 목록에서 이름을 바꾸면 새 이름이 표시되고, 삭제하면 게임이 사라집니다.

## 2️⃣ 도전 기능 가이드

### Lv 9. 끝난 게임 덮어쓰기 막기: 409 `도전`

**API 명세 → [진행과 전체 덱 저장](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html#tag/게임/operation/updateProgress)의 409 응답**

게임 클라이언트는 끝난 게임에 진행 저장 요청을 보내지 않습니다. 하지만 외부에서 직접 API를 호출하면, 이미 끝난 게임(`status`가 `CLEARED`나 `FAILED`)의 최종 기록을 새 내용으로 덮어써 버리는 버그가 있습니다.

- [ ]  끝난 게임인지는 `Game`의 `isFinished()` 메서드로 판정하고, 끝난 게임에 대한 진행 저장 요청은 `ResponseStatusException`으로 409를 반환하고 데이터를 바꾸지 않도록 고치세요.
- [ ]  확인: Postman으로 `FAILED`인 게임에 `PUT /games/{gameId}/progress`를 보내면 409가 옵니다.

  ![끝난 게임 저장 버그 수정 전후의 요청과 응답](docs/images/lv9-check.png)

### Lv 10. 전역 예외 처리: 404·409에 message 붙이기 `도전`

**API 명세 → [에러 응답](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html#section/에러-응답)**

`@RestControllerAdvice`가 붙은 `GlobalExceptionHandler`, 에러 응답 DTO(`ErrorResponse`), 예외 클래스 두 개(`GameNotFoundException`, `GameFinishedException`)는 제공됩니다. 400 응답은 이미 명세 형식으로 나가지만, 404와 409는 아직 스프링 기본 형식이고 `message`가 없습니다.

- [ ]  `GlobalExceptionHandler`에 `GameNotFoundException`을 404로, `GameFinishedException`을 409로 바꾸는 핸들러 두 개를 추가합니다. 제공된 400 핸들러와 `respond` 메서드를 참고합니다.
- [ ]  서비스에서 `ResponseStatusException`으로 만들던 없는 게임(404)과 끝난 게임(409)을 제공된 예외 두 개로 바꿔 던집니다.
- [ ]  확인: Postman으로 `GET http://localhost:8080/games/999`를 호출하면 아래처럼 응답이 명세 형식(`status`, `error`, `message`, `path`)으로 바뀝니다.

  ![404 응답 형식 변경 전후](docs/images/lv10.png)

### Lv 11. N+1 없는 카드 수 집계와 저장 시간 `도전`

**API 명세 → [게임 목록 조회](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html#tag/게임/operation/getGames) 응답의 `createdAt`, `updatedAt`, `deckSize`**

- [ ]  `group by` 쿼리와 N+1 문제에 대해 검색하여 공부하시고 문제를 풀어주세요.
- [ ]  저장 시간: `@EnableJpaAuditing`과 `BaseEntity`를 Game에만 적용해 `createdAt`, `updatedAt`을 목록·상세 응답에 포함합니다.
  - 이 단계 이전에 저장된 게임에는 시각이 비어 있을 수 있습니다.
- [ ]  카드 수: 목록 응답에 저장된 덱의 카드 수(`deckSize`)를 포함합니다. 게임이 N개여도 쿼리 수가 늘지 않아야 합니다(N+1 금지). 기준은 목록 조회가 게임 조회 1회와 카드 수 집계 1회, 총 2회입니다.
  - [ ]  카드 수 집계는 DTO 프로젝션으로 받습니다. 게임 ID와 카드 수를 담는 DTO 클래스를 만들고, JPQL의 `select new 패키지.클래스(...)` 구문으로 `group by` 결과를 그 DTO 목록으로 바로 조회합니다. 인터페이스 프로젝션은 쓰지 않습니다.
  - [ ]  일부러 게임마다 카드 수를 세는 버전을 먼저 만들어 보세요. SQL 로그(`spring.jpa.show-sql=true`)에서 쿼리가 게임 수만큼 늘어나는 것을 확인한 뒤, 상수 개의 쿼리로 고쳐 로그를 비교합니다.

    ![게임 목록 조회 시 Hibernate 쿼리 로그, N+1 버전과 집계 버전](docs/images/lv11-sql.png)
- [ ]  확인: 저장된 여정 목록에 마지막 저장 시간과 "카드 N장"이 표시되고, SQL 로그의 쿼리 수가 게임 수와 무관하게 일정합니다.

  ![저장 시간과 카드 수 적용 전후의 저장된 여정 화면](docs/images/lv11.png)

### Lv 12. 랭킹 `도전`

**API 명세 → [랭킹 조회 `GET /rankings`](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html#tag/랭킹/operation/getRankings)**

랭킹은 우리 DB가 아니라 외부 랭킹 API가 돌려주는 시즌 기록으로 만듭니다. 그 응답에는 버그성 플레이로 보이는 기록과 형식이 어긋난 기록이 섞여 있습니다. 서버가 외부 랭킹 API를 호출해 아래 규칙대로 걸러내고 정렬한 랭킹을 제공하면, 저장된 여정 화면에 랭킹 패널이 나타납니다.

<details>
<summary>외부 랭킹 API</summary>

요청: `GET https://f-api.github.io/game-spring-api-docs/basic/rankings.json`

응답은 한 시즌의 제출 기록 전체를 제출 시각 순서로 담습니다. 필드 이름은 우리 서버의 응답과 다르므로 그대로 옮기지 말고 읽어서 변환해야 합니다. 아래 예시는 실제 응답에서 기록 하나만 남긴 것입니다.

| 응답 필드 | 설명 |
| --- | --- |
| `meta.season.id`, `.name`, `.startsAt`, `.endsAt` | 시즌 식별자, 이름, 기간 |
| `meta.generatedAt`, `meta.schemaVersion`, `meta.totalRecords` | 생성 시각, 응답 형식 버전, 기록 수 |
| `records[]` | 제출 기록 목록 |
| `records[].id` | 기록 ID. 응답 안에서 고유 |
| `records[].submittedAt` | 제출 시각(UTC) |
| `records[].client.version`, `.platform`, `.locale` | 기록을 보낸 게임 클라이언트 정보 |
| `records[].player.id`, `.name`, `.region`, `.tags[]` | 플레이어 ID와 이름, 지역 코드, 태그 목록. 같은 플레이어는 같은 `id`를 가지며 여러 기록을 제출할 수 있음 |
| `records[].run.seed` | 게임 시드 |
| `records[].run.status` | `CLEARED` 또는 `FAILED` |
| `records[].run.clearedFloor` | 클리어한 마지막 층 |
| `records[].run.durationSeconds` | 시작부터 끝까지 걸린 시간(초) |
| `records[].run.finalHp` | 끝난 시점의 HP |
| `records[].run.floors[]` | 층별 로그. `floor`, `enemy`, `turns`, `hpAfter`, `rewards[]`(`offered[]`, `picked`; 고르지 않았으면 `picked`가 `null`, 보상이 없는 층은 `rewards`가 `null`) |
| `records[].bossFight` | 최종 보스전. 10층을 클리어하지 못한 기록은 `null` |
| `records[].bossFight.phases[]` | 페이즈별 기록. `phase`, `turns`, `damageTaken` |
| `records[].bossFight.finishingCard` | 보스를 쓰러뜨린 카드의 카드 타입 |
| `records[].bossFight.totalTurns` | 보스전 총 턴 수라고 제공처가 적어 보낸 값 |
| `records[].deck.size` | 덱 카드 수라고 제공처가 적어 보낸 값 |
| `records[].deck.cards[]` | 끝난 시점의 덱. `cardType`, `acquiredFloor`(시작 덱은 0) |

```json
{
  "meta": {
    "season": {
      "id": "2026-09",
      "name": "붉은 달의 첫 시즌",
      "startsAt": "2026-08-01T00:00:00Z",
      "endsAt": "2026-09-10T00:00:00Z"
    },
    "generatedAt": "2026-09-07T03:00:00Z",
    "schemaVersion": 3,
    "totalRecords": 610
  },
  "records": [
    {
      "id": 5259,
      "submittedAt": "2026-08-17T08:37:00Z",
      "client": {
        "version": "1.4.0",
        "platform": "desktop",
        "locale": "ja-JP"
      },
      "player": {
        "id": "p_2f6e31",
        "name": "먼상인",
        "region": "JP",
        "tags": [
          "first-clear"
        ]
      },
      "run": {
        "seed": "crimson-citadel:5259:2",
        "status": "CLEARED",
        "clearedFloor": 10,
        "durationSeconds": 300,
        "finalHp": 12,
        "floors": [
          {
            "floor": 1,
            "enemy": "GLASS_MITE",
            "turns": 2,
            "hpAfter": 96,
            "rewards": [
              {
                "offered": [
                  "NIGHT_FEAST",
                  "MIST_FORM",
                  "BLOOD_OFFERING"
                ],
                "picked": "BLOOD_OFFERING"
              }
            ]
          },
          {
            "floor": 2,
            "enemy": "BONE_THIEF",
            "turns": 2,
            "hpAfter": 90,
            "rewards": [
              {
                "offered": [
                  "MEND",
                  "IRON_WALL",
                  "BLOOD_TOLL"
                ],
                "picked": "IRON_WALL"
              }
            ]
          },
          {
            "floor": 3,
            "enemy": "RUNE_HOUND",
            "turns": 1,
            "hpAfter": 83,
            "rewards": [
              {
                "offered": [
                  "COUNTER_SIGIL",
                  "VOID_NIGHT",
                  "NIGHT_TITHE"
                ],
                "picked": "NIGHT_TITHE"
              }
            ]
          },
          {
            "floor": 4,
            "enemy": "IRON_WARDEN",
            "turns": 1,
            "hpAfter": 80,
            "rewards": [
              {
                "offered": [
                  "VOID_NIGHT",
                  "SCARLET_MEMORY",
                  "LAST_STAND"
                ],
                "picked": "VOID_NIGHT"
              }
            ]
          },
          {
            "floor": 5,
            "enemy": "THORN_BRIDE",
            "turns": 1,
            "hpAfter": 68,
            "rewards": [
              {
                "offered": [
                  "SEALED_WOUND",
                  "SCARLET_MEMORY"
                ],
                "picked": "SCARLET_MEMORY"
              },
              {
                "offered": [
                  "BLOOD_AMPLIFY",
                  "SECOND_HEART",
                  "SEALED_WOUND"
                ],
                "picked": "SECOND_HEART"
              }
            ]
          },
          {
            "floor": 6,
            "enemy": "VEIL_PRIEST",
            "turns": 1,
            "hpAfter": 58,
            "rewards": [
              {
                "offered": [
                  "BLOOD_RUNE",
                  "MIST_FORM"
                ],
                "picked": "MIST_FORM"
              }
            ]
          },
          {
            "floor": 7,
            "enemy": "BELL_KEEPER",
            "turns": 2,
            "hpAfter": 42,
            "rewards": [
              {
                "offered": [
                  "SUNDER",
                  "BLOOD_RUNE"
                ],
                "picked": "BLOOD_RUNE"
              }
            ]
          },
          {
            "floor": 8,
            "enemy": "EMBER_SEER",
            "turns": 1,
            "hpAfter": 38,
            "rewards": [
              {
                "offered": [
                  "EXECUTION_RUNE",
                  "NIGHT_DANCE",
                  "SHATTER_ARMOR"
                ],
                "picked": "EXECUTION_RUNE"
              }
            ]
          },
          {
            "floor": 9,
            "enemy": "ECLIPSE_DEVOURER",
            "turns": 2,
            "hpAfter": 36,
            "rewards": [
              {
                "offered": [
                  "TENDON_SEVER",
                  "RUNE_SURGE",
                  "QUICK_SLASH"
                ],
                "picked": "TENDON_SEVER"
              },
              {
                "offered": [
                  "NIGHT_DANCE",
                  "ECHO_GUARD"
                ],
                "picked": "NIGHT_DANCE"
              }
            ]
          },
          {
            "floor": 10,
            "enemy": "HOLLOW_TITAN",
            "turns": 1,
            "hpAfter": 12,
            "rewards": null
          }
        ]
      },
      "bossFight": {
        "phases": [
          {
            "phase": "THRONE",
            "turns": 3,
            "damageTaken": 16
          },
          {
            "phase": "UNBOUND",
            "turns": 5,
            "damageTaken": 8
          },
          {
            "phase": "ECLIPSE",
            "turns": 7,
            "damageTaken": 11
          }
        ],
        "finishingCard": "TENDON_SEVER",
        "totalTurns": 15
      },
      "deck": {
        "size": 20,
        "cards": [
          {
            "cardType": "STRIKE",
            "acquiredFloor": 0
          },
          {
            "cardType": "STRIKE",
            "acquiredFloor": 0
          },
          {
            "cardType": "HEART_PIERCE",
            "acquiredFloor": 0
          },
          {
            "cardType": "GUARD",
            "acquiredFloor": 0
          },
          {
            "cardType": "MIST_KNOT",
            "acquiredFloor": 0
          },
          {
            "cardType": "QUICK_SLASH",
            "acquiredFloor": 0
          },
          {
            "cardType": "WARDING_SLASH",
            "acquiredFloor": 0
          },
          {
            "cardType": "BLOOD_RUNE",
            "acquiredFloor": 0
          },
          {
            "cardType": "MEND",
            "acquiredFloor": 0
          },
          {
            "cardType": "BLOOD_OFFERING",
            "acquiredFloor": 1
          },
          {
            "cardType": "IRON_WALL",
            "acquiredFloor": 2
          },
          {
            "cardType": "NIGHT_TITHE",
            "acquiredFloor": 3
          },
          {
            "cardType": "VOID_NIGHT",
            "acquiredFloor": 4
          },
          {
            "cardType": "SCARLET_MEMORY",
            "acquiredFloor": 5
          },
          {
            "cardType": "SECOND_HEART",
            "acquiredFloor": 5
          },
          {
            "cardType": "MIST_FORM",
            "acquiredFloor": 6
          },
          {
            "cardType": "BLOOD_RUNE",
            "acquiredFloor": 7
          },
          {
            "cardType": "EXECUTION_RUNE",
            "acquiredFloor": 8
          },
          {
            "cardType": "TENDON_SEVER",
            "acquiredFloor": 9
          },
          {
            "cardType": "NIGHT_DANCE",
            "acquiredFloor": 9
          }
        ]
      }
    }
  ]
}
```

</details>

- [ ]  다른 서버의 API를 호출하는 `RestClient`는 강의에서 다루지 않았습니다. 아래 코드를 그대로 사용하세요. `RestClient`는 스프링이 제공하는 HTTP 클라이언트로, `retrieve().body(클래스)`가 응답 JSON을 그 클래스의 객체로 바꿔 줍니다. 요청 본문을 받을 때와 같은 방식이라 JSON 필드 이름과 클래스 필드 이름이 같아야 하고, 클래스에는 `@Getter`만 있으면 됩니다.

    ```java
    @Component
    public class RankingClient {

        private static final String SOURCE_URL = "https://f-api.github.io/game-spring-api-docs/basic/rankings.json";

        private final RestClient restClient = RestClient.create();

        public RankingSource fetch() {
            return restClient.get()
                .uri(SOURCE_URL)
                .retrieve()
                .body(RankingSource.class);
        }
    }
    ```

- [ ]  외부 랭킹 API의 응답을 받을 DTO 클래스들을 위 토글의 형식에 맞게 만드세요. 제공 코드가 쓰는 `RankingSource`가 응답 전체이고, 그 안의 객체들은 이름을 자유롭게 정합니다.
- [ ]  아래의 코드를 이용하여 랭킹 API를 구현하세요. 서비스는 외부 랭킹 API의 응답을 받아 다음 순서로 처리한 결과를 응답 DTO `RankingResponse`로 만듭니다.
  1. **순위 대상.** `run.status`가 `CLEARED`이고 `run.clearedFloor`가 `10`인 기록만 순위 대상입니다. 그 밖의 기록은 순위에도, `excludedCount`에도 들어가지 않습니다.
  2. **정상 기록 조건.** 순위 대상 중 아래를 하나라도 어기는 기록은 이상 기록으로 보고 제외하며, 제외한 수가 `excludedCount`입니다.

     | 항목 | 정상 기록의 조건 |
     | --- | --- |
     | 클리어 시간 | `run.durationSeconds`가 층당 30초 이상, 즉 `run.clearedFloor × 30` 이상 |
     | 남은 HP | `run.finalHp`가 1 이상 99 이하 |
     | 덱 크기 | `deck.cards`가 9장 이상 20장 이하이고, `deck.size`가 `deck.cards`의 실제 개수와 같음 |
     | 카드 타입 | 모든 `deck.cards[].cardType`이 카드 타입 목록에 있는 값 |
     | 획득 층 | 모든 `deck.cards[].acquiredFloor`가 0 이상 9 이하 |
     | 보스 페이즈 | `bossFight.phases`가 `THRONE`, `UNBOUND`, `ECLIPSE` 순서로 정확히 3개이고, 각 `turns`가 1 이상이며, `bossFight.totalTurns`가 세 `turns`의 합과 같음 |
     | 마무리 카드 | `bossFight.finishingCard`가 그 기록의 `deck.cards`에 있는 카드 타입 |

  3. **정렬.** 정상 기록을 `run.durationSeconds` 오름차순, 같으면 `run.finalHp` 내림차순, 그래도 같으면 `id` 오름차순으로 정렬합니다.
  4. **플레이어당 하나.** 같은 `player.id`의 정상 기록이 여러 개면 정렬 순서에서 앞선 하나만 남기고, 남은 기록에 1부터 순위를 매깁니다. 응답의 `season`은 `meta.season.id`, `totalRecords`는 `records`의 개수, `bossTurns`는 `bossFight.totalTurns`, `deckSize`는 `deck.cards`의 개수입니다.

    ```java
    @GetMapping("/rankings")
    public ResponseEntity<RankingResponse> getRankings() {
        return ResponseEntity.ok(rankingService.getRankings());
    }
    ```

  - 카드 타입이 게임에 존재하는지 판단할 때 쓸 `CardType` enum은 아래 토글의 코드를 그대로 사용하세요. 명세의 카드 타입 38종입니다.

    <details>
    <summary>ranking/CardType.java</summary>

    ```java
    public enum CardType {
        STRIKE,
        GUARD,
        MIST_KNOT,
        HEAVY_BLOW,
        TENDON_SEVER,
        TWIN_SLASH,
        QUICK_SLASH,
        IRON_WALL,
        BLOOD_RUNE,
        ECHO_GUARD,
        SUNDER,
        MEND,
        COUNTER_SIGIL,
        ARCANE_BOLT,
        WARDING_SLASH,
        RUNE_SURGE,
        SHATTER_BOLT,
        EXECUTION_RUNE,
        BLOOD_AMPLIFY,
        LAST_STAND,
        DECAPITATE,
        NIGHT_DANCE,
        MIST_FORM,
        NIGHT_FEAST,
        SCARLET_MEMORY,
        BLOOD_OFFERING,
        SEALED_WOUND,
        THIRSTING_BLOW,
        BLOOD_TOLL,
        NIGHT_TITHE,
        CRIMSON_RECLAIM,
        HEART_PIERCE,
        SHATTER_ARMOR,
        VOID_NIGHT,
        NIGHT_AFTERIMAGE,
        KILLING_MOMENTUM,
        LINGERING_THUNDER,
        SECOND_HEART
    }
    ```

    </details>

  - 여러 기준으로 목록을 정렬하는 방법(`Comparator`)은 강의에서 다루지 않았습니다. 검색하여 공부하시고 문제를 풀어주세요.
- [ ]  확인: 저장된 여정 화면 아래에 "랭킹" 패널이 나타나고 1위부터 3위까지 표시됩니다. 응답이 명세와 다르거나 순위가 틀리면 게임은 에러 창을 띄우고 시작되지 않습니다. 필드나 타입이 다르면 에러 창에 그 필드 이름이 나오고, 순위가 틀리면 "랭킹 결과가 기대한 순위와 다릅니다"라고만 나오므로 위 규칙을 하나씩 다시 확인합니다.

  ![랭킹 적용 전후의 저장된 여정 화면](docs/images/lv12.png)

---

<aside>
✍🏻

**과제 제출 시에는 아래 질문을 고민해 보고 답변을 함께 제출해 주세요.**

</aside>

1. Controller, Service, Repository는 각각 어떤 역할을 맡나요?
2. `@Service`를 붙이지 않으면 서버가 뜨지 않는 이유는 무엇인가요?
3. `@Transactional(readOnly = true)`는 무슨 뜻이며, 저장하는 메서드에 붙이면 왜 안 되나요?
4. `@NotBlank`, `@NotNull`, `@NotEmpty`는 각각 어떤 값을 걸러내나요?
5. 엔티티를 그대로 응답하지 않고 DTO로 바꿔서 응답하는 이유는 무엇인가요?
6. 이름 변경에서 `save()`를 호출하지 않았는데 DB에 반영되는 이유는 무엇인가요?
