# DDD (Domain-Driven Design)

비즈니스 도메인을 코드의 중심에 두고, **Aggregate · Entity · Value Object**로 도메인을
직접 모델링하는 아키텍처입니다. 로직을 Service가 아니라 **도메인 모델 스스로**가 갖게 하여
"빈약한 도메인(Anemic Domain)"에서 벗어나는 것이 핵심입니다. 전체 프로젝트 개요와 공통
스펙은 [루트 README](../README.md)를 참고하세요.

이 구현이 정한 Aggregate 경계와 상세 규칙은 [`architecture.md`](./architecture.md)를 참고하세요.

---

## 📜 유래 (누가 · 언제)

- **Eric Evans, _Domain-Driven Design: Tackling Complexity in the Heart of Software_ (2003)**
  에서 정립된 접근으로, "블루북"이라 불립니다.
- 이후 **Vaughn Vernon, _Implementing Domain-Driven Design_ (2013, "레드북")** 이
  Aggregate 설계·Bounded Context 등 실무 적용을 구체화했습니다.
- 복잡한 도메인일수록 기술이 아니라 **도메인 지식과 모델링**이 설계를 이끌어야 한다는
  철학을 담고 있습니다. (전술적 패턴 + 전략적 설계)

---

## 🧭 핵심 아이디어

> **데이터를 가진 쪽이 그 데이터의 규칙도 가진다.**
> 로직이 Service로 새어 나가면, 그 순간 도메인은 DTO가 됩니다.

```
presentation  ──▶  application  ──▶  domain  ◀──  infrastructure
                                       ▲
                        모든 의존성이 domain을 향한다
                        domain은 아무것도 모른다
```

| 구성요소 | 정체성 | 책임 | 밖으로 나가는가 |
|---|---|---|---|
| **Value Object** | 없음 (값으로 비교) | 값의 의미와 검증 (`Money`, `Email`) | O (불변이라 안전) |
| **Entity** | 있음 (ID로 비교) | 자기 상태의 불변식 | X |
| **Aggregate Root** | 있음 | 묶음 전체의 일관성 · 외부의 유일한 입구 | X |
| **Domain Service** | 없음 | 한 객체에 담기지 않는 규칙 | - |
| **Repository** | - | Aggregate 단위 저장/조회 (interface는 domain) | - |
| **Application Service** | - | 실행 순서 · 트랜잭션 · 외부 호출 | - |

**세 가지 경계**

1. **Aggregate** — 한 트랜잭션에서 함께 지켜져야 하는 불변식의 단위.
   외부는 Root를 통해서만 내부에 닿습니다.
2. **Bounded Context** — 같은 단어가 다른 의미를 갖는 경계.
   `Order`의 `User`와 `Payment`의 `User`는 다른 모델입니다.
3. **Layer** — 의존 방향의 경계. `domain`은 Spring도 JPA도 모릅니다.

---

## 📂 패키지 구조

```
ddd/
├── order/                              # Bounded Context
│   ├── domain/                         # ⬅ 아무것도 의존하지 않는다
│   │   ├── model/
│   │   │   ├── Order.java              # Aggregate Root
│   │   │   ├── OrderLine.java          # Aggregate 내부 Entity
│   │   │   ├── OrderStatus.java
│   │   │   └── vo/
│   │   │       ├── OrderId.java
│   │   │       ├── Money.java
│   │   │       └── Quantity.java
│   │   ├── service/
│   │   │   └── OrderPricingService.java    # Domain Service
│   │   └── repository/
│   │       └── OrderRepository.java        # interface (구현 X)
│   │
│   ├── application/
│   │   ├── PlaceOrderService.java      # 흐름 조립 · @Transactional
│   │   └── dto/
│   │       ├── PlaceOrderCommand.java
│   │       └── OrderInfo.java              # 결과 DTO 는 {도메인}Info 로 통일
│   │
│   ├── infrastructure/
│   │   ├── persistence/
│   │   │   ├── OrderJpaEntity.java     # DB 스키마용 (도메인 아님)
│   │   │   ├── OrderMapper.java
│   │   │   └── OrderRepositoryImpl.java    # ⬅ domain의 interface 구현
│   │   └── client/
│   │       └── PaymentApiClient.java
│   │
│   └── presentation/
│       ├── OrderController.java
│       └── dto/
│           ├── OrderRequest.java
│           └── OrderResponse.java
│
└── product/                            # 또 하나의 Bounded Context
    └── (동일 구조)
```

> `domain` 폴더에서 `import org.springframework...` / `import jakarta.persistence...`
> 가 하나라도 보이면, 그 순간 이 구조는 무너진 것입니다.

---

## 📌 주요 규칙

| # | 규칙 | X | O |
|---|---|---|---|
| 1 | 원시값 대신 VO | `Long amount` | `Money amount` |
| 2 | Aggregate 간 참조는 ID로 | `Order.user: User` | `Order.userId: UserId` |
| 3 | 여러 Aggregate 변경은 한 트랜잭션에서 조율 | Service가 규칙 판단 | Aggregate가 판단, Service는 순서만 |
| 4 | Repository는 Root 단위 | `OrderLineRepository` | `OrderRepository` |
| 5 | 도메인 노출 금지 | `Order` 반환 | `OrderResponse` 반환 |

**Domain**

1. `domain`은 다른 레이어·프레임워크·DB를 알지 못합니다.
2. 로직은 Entity / VO에 **먼저** 놓고, 거기 담기지 않는 것만 Domain Service로 뺍니다.
3. Entity는 항상 유효한 상태로만 존재합니다. 생성 시점에 불변식을 검증하고, 의미 없는
   setter를 열지 않습니다.
4. VO는 불변이며 값으로 비교합니다.

**Aggregate**

5. 외부는 **Root를 통해서만** 내부에 접근합니다. 내부 Entity를 밖으로 반환하지 않습니다.
6. Aggregate 간 참조는 객체가 아니라 **식별자(ID)** 로 합니다.
7. Aggregate는 작게 유지합니다. 포함 근거는 "함께 지켜져야 할 규칙"뿐이며,
   **조회 편의는 근거가 아닙니다.**
8. 여러 Aggregate가 함께 성공/실패해야 하는 use case(주문, 가입→쿠폰, 상품→기본옵션)는
   **하나의 트랜잭션**으로 조율합니다. 각 규칙은 여전히 해당 Aggregate가 스스로 판정하고,
   Application Service는 실행 순서를 세우고 실패 시 롤백으로 원자성을 보장할 뿐입니다.
   (이 구현은 Domain Event / 결과적 일관성을 사용하지 않습니다 — 아래 *경계에 대한 선택* 참고.)

**Repository / Application**

9. Repository는 Aggregate Root 단위로만 만들고, interface는 `domain`에 둡니다.
10. Application Service는 **판단하지 않습니다.** 분기·계산이 늘어난다면 그 규칙이
    도메인으로 가야 한다는 신호입니다.
11. 트랜잭션 범위는 Application Service가 관리합니다.
12. Application Service는 다른 Application Service를 호출하지 않습니다.
13. Presentation에는 도메인 객체가 아니라 DTO를 반환합니다.

**Context**

14. 한 Context가 다른 Context의 데이터를 써야 하면, 도메인 객체를 공유하지 않고
    **원시값·VO만 주고받는 계약(port)** 으로 받습니다. (예: `OrderExistencePort` —
    product가 order 내부를 모른 채 "주문 존재 여부"만 물어봄)

---

## ⚖️ 장점 · 단점

**장점**

- **로직의 위치가 예측됩니다.** "주문 금액 규칙"을 찾을 때 뒤질 곳은 `Order` 하나입니다.
  Service를 전수 조사할 필요가 없습니다.
- **불변식이 한 곳에서 보장됩니다.** Aggregate Root를 통과하지 않고 상태를 바꿀 방법이
  없으므로, 검증 누락으로 인한 잘못된 데이터가 원천 차단됩니다.
- **도메인이 프레임워크와 분리됩니다.** JPA를 걷어내도, 웹을 배치로 바꿔도 `domain`은
  그대로입니다. 테스트도 DB 없이 순수 단위 테스트로 끝납니다.
- **용어가 통일됩니다.** 기획서의 단어와 클래스 이름이 일치해(Ubiquitous Language)
  대화 비용이 줄어듭니다.
- **경계가 문서 없이 드러납니다.** Aggregate 경계 = 트랜잭션 경계이므로, 동시성·정합성
  논의를 코드 구조 위에서 할 수 있습니다.

**단점**

- **초기 비용이 큽니다.** 도메인 모델과 JPA 엔티티를 분리하면 Mapper가 늘고, VO 하나
  만드는 데도 클래스가 하나씩 생깁니다.
- **모델링 실력에 결과가 좌우됩니다.** Aggregate 경계를 잘못 그으면 트랜잭션이 뒤엉키고,
  차라리 Layered보다 못한 구조가 됩니다. 경계 재조정 비용도 큽니다.
- **조회에 불리합니다.** 화면 하나에 여러 Aggregate가 필요하면 N번 조회하게 됩니다.
  결국 조회 전용 모델(CQRS)을 추가로 도입해야 하는 경우가 많습니다.
- **여러 Aggregate를 한 트랜잭션에서 조율하면 결합이 생깁니다.** 원자성을 위해 Application
  Service가 여러 Aggregate의 Repository를 함께 다루게 되고, 그만큼 use case가 무거워집니다.
  (아래 *경계에 대한 선택* 참고.)
- **단순 CRUD에는 과합니다.** 규칙이 거의 없는 도메인에 적용하면 파일만 늘어납니다.

> **적용 범위:** 모든 도메인에 균일하게 적용하기 위한 아키텍처가 아닙니다.
> 규칙이 복잡하고 자주 바뀌는 **핵심 도메인(Core Domain)** 에 집중하고,
> 단순 CRUD 영역은 가볍게 유지하는 것을 권장합니다.

---

## 🧩 경계에 대한 선택 — Domain Event를 쓰지 않는다

교과서적인 DDD는 "한 트랜잭션 = 한 Aggregate"를 지키기 위해, Aggregate 경계를 넘는 변경을
**Domain Event + 결과적 일관성**으로 처리하라고 말합니다. **이 구현은 그 규칙을 따르지 않습니다.**

이 도메인의 핵심 흐름(주문 / 주문 취소)은 재고 · 포인트 · 쿠폰 · 주문이 **함께 성공하거나 함께
실패해야** 합니다. 이 원자성을 Event로 쪼개면 보상 트랜잭션 · 실패 재처리가 따라오는데, 이 규모의
학습 프로젝트에서는 이득 없이 복잡도만 커집니다. 그래서 **여러 Aggregate가 함께 변해야 하는
use case는 하나의 트랜잭션(Application Service)으로 조율**하고, 실패 시 롤백으로 원자성을 지킵니다.

- **주문(order-01)** — 옵션 재고 차감 · 쿠폰 사용 · 포인트 차감 · 주문 생성을 한 트랜잭션에서 처리
- **주문 취소(order-04)** — 재고 · 쿠폰 · 포인트 복원 후 주문 삭제를 한 트랜잭션에서 처리
- **회원 가입(user-01)** — User 저장과 첫 쿠폰 발급을 한 트랜잭션에서 처리
- **상품 등록(product-01)** — Product 저장과 기본 옵션 생성을 한 트랜잭션에서 처리

**포기하지 않은 것:** 규칙은 여전히 데이터를 가진 Aggregate가 소유합니다(재고 부족은 `Option`,
포인트 부족은 `User`/`Point`, 쿠폰 소유·소진은 `Coupon`, 금액 계산은 `Order`). Application
Service는 **판단하지 않고** 순서를 세우고 트랜잭션 경계만 관리합니다.

**얻는 것 / 잃는 것:** 원자성과 단순함을 얻는 대신, Application Service가 여러 Aggregate의
Repository를 함께 다루게 되어 결합이 생깁니다. Context를 넘는 읽기/판정은 도메인을 노출하지 않도록
원시값 계약(port)으로 감쌉니다(`OrderExistencePort`).

---

## 🛠️ 기술 스택

- Java 21 · Spring Boot 3.3.0 (Web / Data JPA / Validation) · MySQL · Lombok · Gradle

## ✅ 구현 현황

구현 코드는 [`shop/`](./shop) 아래에 있으며, `com.example.shop` 밑을 **bounded context 단위**
(`user` · `coupon` · `product` · `order`)로 나누고 각 context를 `domain / application /
infrastructure / presentation` 4계층으로 둡니다. `domain`에는 프레임워크·JPA import가 없으며,
JPA 엔티티는 도메인 모델과 분리되어 `infrastructure`의 Mapper가 변환합니다.

- **user** (user-01~04), **coupon** (coupon-01~02), **product/option** (product-01~05,
  option-01~02), **order** (order-01~04) 전부 구현
- 실행: `shop/`에서 `./gradlew bootRun` (MySQL `localhost:3306/shop` 필요, 포트 8080)
- 검증: 루트의 [`test.http`](../test.http) S01~S53 시나리오 공통 사용