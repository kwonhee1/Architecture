# Layered Architecture (계층형 아키텍처)

애플리케이션을 역할이 다른 **수평 계층(layer)** 으로 쌓아 올리고, 요청이 위에서 아래로
한 방향으로만 흐르도록 구성하는 아키텍처입니다. 전체 프로젝트 개요와 공통 스펙은
[루트 README](../README.md)를 참고하세요.

---

## 📜 배경

이 프로젝트의 **출발점**입니다. 가장 널리 쓰이고 러닝커브가 낮은 구조를 먼저 구현해
기준선을 만들고, 여기서 드러나는 문제를 다른 아키텍처들이 어떻게 푸는지 비교합니다.

### 유래

- 계층으로 소프트웨어를 나누는 발상은 **Edsger Dijkstra의 THE 운영체제(1968)** 계층 구조,
  그리고 네트워크의 **OSI 7계층 모델**로 거슬러 올라갑니다.
- 엔터프라이즈 애플리케이션의 표준 패턴으로 정리된 것은 **Martin Fowler,
  _Patterns of Enterprise Application Architecture_ (2002)** 입니다.
  여기서 Presentation / Domain(Business) / Data Source 3계층이 정형화되었습니다.
- 오늘날 대부분의 웹 프레임워크(Spring 포함)에서 **가장 기본이자 출발점**이 되는 구조로,
  "Controller → Service → Repository" 형태로 관습화되어 있습니다.

### 이 구현이 택한 것

계층형은 **cross-service 참조를 허용**합니다. 여러 도메인을 아우르는 로직은 상위 도메인
Service가 하위 도메인 Service들을 주입받아 조합합니다. Service끼리 직접 참조하는 것을
계층형의 자연스러운 확장으로 보고, 그대로 밀어붙였을 때 무엇이 걸리는지 확인합니다.

---

## 🧭 계층 구성

```
controller → service → repository
```

| 계층 | 책임 | 하지 않는 것 |
|------|------|--------------|
| **controller**<br>`@RestController` | 요청/응답 매핑, DTO 변환, 인증 정보 수신 | 로직을 갖지 않음<br>repository를 직접 호출하지 않음 |
| **service**<br>`@Service` | 도메인 로직(규칙 검증·계산), 실행 순서, 트랜잭션 경계 | — |
| **repository**<br>`JpaRepository` | DB 접근 | 로직을 갖지 않음 |

- 요청은 항상 **위 → 아래** 단방향으로 흐름
- 단, **service끼리는 옆으로 참조 가능** — 도메인 조합이 필요하면 상위 service가 하위
  service를 주입받음 (`OrderService` → `ProductService` · `CouponService` · `UserService`)
- 최상위 패키지는 계층이 아니라 **도메인으로 먼저 나눔**(package-by-feature).
  도메인 안에서 다시 controller / service / repository / entity / dto 로 나눔

---

## 📌 주요 규칙

- **단방향 의존성** — 의존성 방향은 위 → 아래 뿐. repository가 service를, service가
  controller를 알지 못함
- **계층 건너뛰기 금지** — controller는 repository를 직접 호출하지 않고 반드시 service를 거침
- **트랜잭션 경계는 service** — `@Transactional`은 service에 둠
- **cross-service 참조 허용** — 여러 도메인에 걸친 로직은 상위 도메인 service가 하위 도메인
  service들을 조합해 처리
- **DTO로 계층 경계 통과** — 엔티티를 그대로 노출하지 않고 요청/응답은 DTO로 변환

---

## 🔑 주요 로직 흐름

이 프로젝트의 핵심인 **주문(order-01)** 은 재고 · 포인트 · 쿠폰 · 주문이 한 트랜잭션에서
함께 움직입니다. 계층형에서는 이 흐름 전체를 **`OrderService` 하나가 떠안습니다.**

```
OrderController
   │
   ▼
OrderService.create()            @Transactional ← 트랜잭션 경계
   ├─ UserService          유저 조회
   ├─ CouponService        쿠폰 조회 → coupon.use(user)  (소유/사용여부 검증, 할인액 반환)
   ├─ ProductOptionService 옵션 조회 → option.decreaseStock()  (재고 차감)
   ├─ (동일 옵션 합산 · 주문 금액 계산)
   ├─ OrderRepository      주문 저장
   └─ user.usePoint()      포인트 차감  (부족하면 예외)
```

- 어느 단계에서 예외가 나도 `OrderService.create()`의 트랜잭션 경계에서 **전체가 롤백**됩니다
- **주문 취소(order-04)** 도 같은 자리에서 처리됩니다 — 소유자 확인 → `option.increaseStock()`
  (재고 복원) → `coupon.restore()` (쿠폰 복원) → `user.refundPoint()` (포인트 환불) →
  주문 삭제

### 여기서 드러나는 것

- `OrderService`가 **4개 도메인의 service를 주입**받습니다. 주문 흐름이 곧 order 도메인의
  로직 자리에 들어와 있습니다
- 다른 도메인이 쓰라고 열어 둔 함수(`getEntity()`, `getEntityWithProduct()`)와 controller가
  쓰는 함수가 **시그니처로 구분되지 않습니다**. 둘 다 그냥 public 입니다
- 그래서 다른 도메인이 엔티티를 받아 **getter로 직접 로직을 짜는 것을 막을 방법이 없습니다**

> 이 지점이 [Facade](../Facade) 구현의 출발점입니다.

---

## ⚖️ 장점 · 단점

### 장점
- **단순하고 직관적** — 러닝커브가 낮고, 어디에 무엇을 둘지 규칙이 명확
- **역할 분리** — 각 계층의 책임이 뚜렷해 코드 위치를 예측하기 쉬움
- **생산성** — 프레임워크 관습과 일치해 빠르게 개발 가능. 대부분의 CRUD에 충분
- **테스트 용이** — 계층 단위로 목(mock)을 끼워 넣기 쉬움

### 단점
- **Service 비대화** — 비즈니스 로직이 service에 몰려 거대한 서비스가 되기 쉬움
- **cross-service 결합** — service끼리 직접 참조하다 보면 도메인 간 결합이 강해지고,
  순환 참조·의존성 방향이 흐려질 위험이 있음
- **도메인 로직의 누수** — 도메인 경계를 넘어 다른 도메인의 로직을 조립하는 것을
  구조적으로 막지 못함
- **도메인 빈약(Anemic Domain)** — 엔티티가 데이터 홀더에 그치고 로직이 service로 새어 나감
- **인프라 종속** — 도메인이 JPA/DB 같은 하위 계층 기술에 직접 의존하게 됨

> 이 단점들을 다른 아키텍처(Facade / Ports & Adapters / DDD)가 어떻게 다르게 푸는지
> 비교하는 것이 이 프로젝트의 목적입니다.

---

## 🛠️ 기술 스택

- Java 21 · Spring Boot 3.3.0 (Web / Data JPA / Validation) · MySQL · Lombok · Gradle
