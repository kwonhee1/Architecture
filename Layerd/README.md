# Layered Architecture (계층형 아키텍처)

애플리케이션을 역할이 다른 **수평 계층(layer)** 으로 쌓아 올리고, 요청이 위에서 아래로
한 방향으로만 흐르도록 구성하는 아키텍처입니다. 전체 프로젝트 개요와 공통 스펙은
[루트 README](../README.md)를 참고하세요.

---

## 📜 유래 (누가 · 언제)

- 계층으로 소프트웨어를 나누는 발상은 1960~70년대 **Edsger Dijkstra의 THE 운영체제(1968)**
  계층 구조, 그리고 네트워크의 **OSI 7계층 모델**로 거슬러 올라갑니다.
- 엔터프라이즈 애플리케이션의 표준 패턴으로 정리된 것은 **Martin Fowler,
  _Patterns of Enterprise Application Architecture_ (2002)** 입니다.
  여기서 Presentation / Domain(Business) / Data Source 3계층이 정형화되었습니다.
- 오늘날 대부분의 웹 프레임워크(Spring 포함)에서 **가장 기본이자 출발점**이 되는 구조로,
  "Controller → Service → Repository" 형태로 관습화되어 있습니다.

---

## 🧭 핵심 아이디어

```
HTTP 요청
   │
   ▼
[ Controller ]  요청/응답 매핑, DTO 변환, 인증 정보 수신
   │
   ▼
[ Service ]     비즈니스 로직 + 트랜잭션 경계(@Transactional)
   │
   ▼
[ Repository ]  JPA 영속성 처리
   │
   ▼
[ Database ]
```

- **Controller** (`@RestController`) — HTTP 관심사만. 로직을 갖지 않음
- **Service** (`@Service`) — 규칙 검증·계산·트랜잭션 책임
- **Repository** (`JpaRepository`) — DB 접근만
- 요청은 항상 **위 → 아래** 단방향으로 흐름

---

## 📂 패키지 구조

**도메인 우선(package-by-feature)** — 최상위를 계층이 아니라 도메인으로 먼저 나누고,
각 도메인 안에서 다시 계층으로 나눕니다.

```
src/main/java/com/example/shop
├── ShopApplication.java
├── common/                       # 계층을 가로지르는 공통 관심사
│   ├── auth/                     # 인증 필터
│   ├── config/                   # 설정
│   └── exception/                # 전역 예외 처리
└── domain/
    ├── user/
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── entity/
    │   └── dto/
    ├── coupon/   ├── controller  ├── service  ├── repository  ├── entity  └── dto
    ├── product/  ├── controller  ├── service  ├── repository  ├── entity  └── dto
    ├── option/   ├── controller  ├── service  ├── repository  ├── entity  └── dto
    └── order/    ├── controller  ├── service  ├── repository  ├── entity  └── dto
```

---

## 📌 주요 규칙

- **단방향 의존성** — 의존성 방향은 위 → 아래 뿐. Repository가 Service를, Service가
  Controller를 알지 못함
- **계층 건너뛰기 금지** — Controller는 Repository를 직접 호출하지 않고 반드시 Service를 거침
- **트랜잭션 경계는 Service** — `@Transactional`은 Service에 둠
- **cross-service 참조 허용** — 여러 도메인을 아우르는 로직은 상위 도메인 Service가
  하위 도메인 Service들을 주입받아 조합함 (예: 주문 Service가 상품·쿠폰·유저 Service를 참조).
  Service끼리 직접 참조하는 것을 계층형의 자연스러운 확장으로 봄
- **DTO로 계층 경계 통과** — 엔티티를 그대로 노출하지 않고 요청/응답은 DTO로 변환

---

## ⚖️ 장점 · 단점

### 장점
- **단순하고 직관적** — 러닝커브가 낮고, 어디에 무엇을 둘지 규칙이 명확
- **역할 분리** — 각 계층의 책임이 뚜렷해 코드 위치를 예측하기 쉬움
- **생산성** — 프레임워크 관습과 일치해 빠르게 개발 가능. 대부분의 CRUD에 충분
- **테스트 용이** — 계층 단위로 목(mock)을 끼워 넣기 쉬움

### 단점
- **Service 비대화** — 비즈니스 로직이 Service에 몰려 거대한 서비스가 되기 쉬움
- **cross-service 결합** — Service끼리 직접 참조하다 보면 도메인 간 결합이 강해지고,
  순환 참조·의존성 방향이 흐려질 위험이 있음
- **도메인 빈약(Anemic Domain)** — 엔티티가 데이터 홀더에 그치고 로직이 Service로 새어 나감
- **인프라 종속** — 도메인이 JPA/DB 같은 하위 계층 기술에 직접 의존하게 됨

> 이 단점들을 다른 아키텍처(Facade / Ports & Adapters / DDD)가 어떻게 다르게 푸는지
> 비교하는 것이 이 프로젝트의 목적입니다.

---

## 🛠️ 기술 스택

- Java 21 · Spring Boot 3.3.0 (Web / Data JPA / Validation) · MySQL · Lombok · Gradle
