# Facade Architecture (Application / Domain Service)

정립된 아키텍처가 아니라, **계층형(Layerd) 구현에서 부딪힌 문제를 풀기 위해 직접 정의한 구조**입니다. 목적은 하나 — **도메인 로직이 섞이는 것을 규칙이 아니라 구조로 막는 것.**
전체 프로젝트 개요와 공통 스펙은 [루트 README](../README.md)를 참고하세요.

---

## 📜 배경

**도메인 로직이 섞이는 것을 구조적으로 막기위한 아키텍쳐**

Service를 **application service(= facade service)** 와 **domain service** 두 종류로 나누고, **도메인 로직은 오직 자기 도메인의 domain service 안에만** 두는 아키텍처입니다.

이름은 Facade지만 **GoF의 Facade 패턴과는 목적이 다릅니다.** 자세한 규칙은 [`architecture.md`](./architecture.md)를 참고하세요.

### 계층형에서 드러난 문제

계층형은 cross-service 참조를 허용하므로, 주문 흐름에서 `OrderService`가 `ProductService`를
직접 주입받아 호출합니다. 여기서 네 가지가 겹칩니다.

1. **product 구매 처리는 `order.create()`의 로직이 아니다** — 그런데 order의 흐름 안에
   들어와 있음
2. **service의 public 함수가 "controller용"인지 "다른 도메인용"인지 시그니처로 구분되지
   않는다** — 둘 다 그냥 public
3. **캡슐화 함수를 만들어도 기존 함수가 남는다** — product service에 `purchase()`를 만들어
   구매 로직을 감쌌지만, controller가 상품 정보 DTO를 받아야 하므로 `getPurchase()`가
   그대로 남아 있음
4. 그래서 **다른 도메인이 controller용 함수를 호출해 `product`의 getter로 로직을 짜는 것이
   가능하다**

즉 캡슐화를 해 두어도 **우회할 문이 열려 있는 상태**입니다. 이것을 코드 리뷰가 아니라
**아키텍처적으로 원천 봉쇄**하려는 것이 출발점입니다.

### 도입한 것

- domain service인 order service는 **product service를 호출하지 않는다**
- facade service가 product service를 통해 처리를 끝내고, order service에는 **order 생성에
  필요한 정보만** 넘긴다

### 목표점

- **order service와 product service는 섞이지 않는다**
  → 조율은 facade service의 책임이므로, order service가 product service에 의존할 이유가
  없어짐
- **facade service에 도메인 로직을 두지 않기로 했으므로, product service의 캡슐화 함수가
  필요해진다**
  → facade에서 `product`의 getter로 값을 얻어 if문을 돌리는 것이 금지이므로,
  `product.purchase()` 함수가 **필요해서 생김**

두 번째가 이 구조의 핵심입니다. 캡슐화를 권장하는 대신, **캡슐화하지 않으면 코드를 쓸 수
없게** 만듭니다.

---

## 🧭 핵심 아이디어 — 계층 구성

```
controller → facade service → domain service → repository
```

| 계층 | 책임 | 하지 않는 것 |
|------|------|--------------|
| **controller** | 요청/응답 매핑, DTO 변환, 인증 정보 수신 | 로직을 갖지 않음 |
| **facade service**<br>(application service) | 실행 순서, 트랜잭션 경계, 외부 adapter 호출 | **도메인 로직을 갖지 않음**<br>다른 facade를 호출하지 않음 |
| **domain service** | 자기 도메인의 규칙 검증·계산 | **다른 도메인 service를 호출하지 않음** |
| **repository** | DB 접근 | — |

- 도메인을 조합하는 유일한 지점은 **facade service** — 하나의 facade가 여러 도메인의
  domain service를 순서대로 호출
- domain service는 서로를 모름 — 옆으로 뻗는 화살표가 존재하지 않음
- 그래서 의존성은 항상 **controller → facade → domain service → repository** 한 방향

---

## 📌 주요 규칙

- **domain service는 다른 domain의 service를 호출하지 않는다** — 도메인 간 조합은 오직
  facade에서
- **facade service는 다른 facade service를 호출하지 않는다** — facade끼리 엮이면
  계층형의 cross-service 결합이 그대로 재현되므로 금지
- **facade는 도메인 로직을 처리하지 않는다** — 검증·계산은 domain service에게 시키고,
  facade는 순서만 결정
- **domain service의 파라미터는 Domain / VO 타입으로 받는다**
  ```
  Long userId  ✗        // 원시 타입으로 다른 도메인을 표현하지 않음
  User user    ✓
  UserId userId ✓
  ```
- **트랜잭션 경계는 facade(application service)** — `@Transactional`은 facade에 둠
- **외부 adapter 호출은 facade의 책임** — 이 아키텍처는 port/adapter 개념을 직접 다루지
  않지만(필요하면 도입), 도메인 로직과 분리된 외부 호출은 facade에서 처리할 것을 권장
- **DTO로 경계 통과** — 엔티티를 그대로 노출하지 않음

---

## 🔑 주문 흐름은 어떻게 풀리는가

이 프로젝트의 핵심인 **주문(order-01)** 은 재고 · 포인트 · 쿠폰 · 주문이 한 트랜잭션에서
함께 움직입니다. 계층형에서는 `OrderService`가 다른 Service들을 직접 참조해 이 흐름을
전부 떠안지만, 여기서는 책임이 이렇게 갈립니다.

- `OrderFacade` — 재고 확인 → 포인트 확인 → 쿠폰 확인 → (재고 차감 · 포인트 차감 ·
  쿠폰 사용) → 주문 생성의 **순서와 트랜잭션**만 담당
- `OptionService` — 재고 확인/차감 규칙, `ProductService` — 상품 규칙,
  `UserService` — 포인트 규칙, `CouponService` — 쿠폰 사용 가능 여부와 할인 계산,
  `OrderService` — 주문 자체의 규칙

어느 단계가 실패해도 facade의 트랜잭션 경계에서 **전체가 롤백**됩니다.
주문 취소(order-04)도 동일하게 facade가 각 domain service의 롤백 로직을 순서대로 호출합니다.

---

## ⚖️ 장점 · 단점

> **도입 이유** — 도메인 로직이 섞이는 것을 규칙이 아니라 구조로 막기 위함.

### 장점
- **도메인 로직이 섞이지 않는다** — 각 domain service를 호출하는 주체가 facade 하나뿐이므로,
  한 도메인의 로직이 다른 도메인의 흐름 안으로 흘러 들어갈 수 없음
- **domain service는 자기 도메인만 알면 된다** — 다른 도메인의 존재를 몰라도 되고, 필요한
  값은 파라미터로 요청하기만 하면 되므로(그 값을 구해 오는 것은 facade의 몫) 구현이 편함.
  순환 참조가 구조적으로 불가능하고 단독 테스트도 쉬움
- **Service 비대화 완화** — "조합"과 "로직"이 분리되어 계층형의 거대 Service 문제가 줄어듦
- **낮은 도입 비용** — Aggregate/VO 모델링 없이 서비스 분리만으로 도메인 경계를 확보.
  계층형에서 넘어오기 쉬움

### 단점
- **도메인 로직과 절차 로직의 경계가 주관적** — 어디까지가 도메인 규칙이고 어디부터가 흐름
  제어인지 사람마다 판단이 달라, 팀 합의가 없으면 흔들림
- **facade의 mapping 코드 증가** — domain service가 요구하는 파라미터를 facade가 전부
  조회해서 넘겨줘야 하므로, 조회·변환 코드가 facade에 쌓임
- **클래스 수 증가** — 도메인마다 facade + domain service로 나뉘어 파일이 늘고, 단순 CRUD는
  facade가 그냥 통과(pass-through)만 하는 껍데기가 되기 쉬움
- **facade 간 재사용 불가** — facade끼리 호출 금지라 공통 흐름이 있어도 중복될 수 있음

---

## 🛠️ 기술 스택

- Java 21 · Spring Boot 3.3.0 (Web / Data JPA / Validation) · MySQL · Lombok · Gradle

> 현재 이 폴더는 설계 문서 단계이며, 구현 코드는 위 구조를 따라 추가될 예정입니다.
