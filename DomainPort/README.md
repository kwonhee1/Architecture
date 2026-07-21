# DomainPort Architecture (UseCase / DomainPort 계약 분리)

정립된 아키텍처가 아니라, **service의 어떤 함수를 누가 호출할 수 있는지를 타입으로 못 박기 위해
직접 정의한 구조**입니다. 목적은 하나 — **도메인 로직이 밖으로 새어 나가는 통로를 없애는 것.**
전체 프로젝트 개요와 공통 스펙은 [루트 README](../README.md)를 참고하세요.

자세한 규칙은 [`architecture.md`](./architecture.md)를 참고하세요.

---

## 📜 배경

> service 함수들에서 **"controller용 public"** 과 **"다른 도메인용 public"** 이
> **시그니처상 구분되지 않는다.**

`ProductService` 안에는 controller에게 상품 정보를 내주는 함수와, 주문 흐름에서 쓰라고 열어 둔
함수가 나란히 public으로 놓입니다. 둘을 구분하는 것은 **개발자의 기억과 코드 리뷰뿐**입니다.

그래서 다른 도메인이 controller용 함수를 호출해 **`Product`를 통째로 받아 가고, getter로 자기
쪽에서 로직을 짜는 것**이 가능합니다. 캡슐화 함수를 아무리 잘 만들어 두어도 **우회할 문이 열려
있는 상태**입니다.

이것을 아키텍쳐로 해결하는 것이 출발점 입니다

### 도입한 것

**서비스 표면(surface)을 두 개의 계약(interface)으로 쪼갭니다.**

```
controller  ──▶  UseCase       controller가 사용할 수 있는 함수 명세
타 domain   ──▶  DomainPort    다른 domain이 사용할 수 있는 함수 명세
                    │
                    └──▶ Service (두 interface를 모두 구현)
```

호출자는 구현체(`ProductService`)가 아니라 **자기 몫의 interface만 주입**받습니다.
controller는 `ProductUseCase`를, 다른 도메인은 `ProductPort`를 봅니다.

**보이지 않는 함수는 호출할 수 없습니다.** 캡슐화가 권장이 아니라 컴파일 조건이 됩니다.

---

## 🧭 핵심 아이디어

| 구성요소 | 누가 의존하는가 | 무엇을 노출하는가 |
|----------|-----------------|-------------------|
| **UseCase** | controller | 화면/응답에 필요한 조회·명령, dto 반환 |
| **DomainPort** | 다른 도메인의 service | 그 도메인에게 **실행시킬 행동**, vo 반환 |
| **Service** | — (구현체) | 두 interface를 구현. 자기 도메인 로직의 유일한 주인 |
| **Repository** | 자기 도메인 service | DB 접근 |

> **A 도메인은 언제나 A의 Port를 걸쳐야 한다.**

- B가 A의 데이터를 받아 B 안에서 판단하는 일이 없도록, port는 **결과를 돌려주지 재료를
  돌려주지 않습니다**
- domain은 경계를 넘지 않으므로, 애초에 다른 도메인에는 **판단할 재료가 없습니다**

---

## 📌 주요 규칙

### service

- **다른 도메인의 service를 직접 사용할 수 없다** — 대신 **DomainPort는 사용 가능**
- 다른 도메인의 로직은 **무조건 domain port를 거쳐서** 실행한다
  (다른 도메인의 함수를 직접 호출하지 않는다)

### port

- **output은 VO를 사용한다**
- **호출에 따른 output이 아니라, "행동"에 따른 충분한 반환값을 반환한다**
  - 반환값은 일어난 행동을 **설명할 만큼**
  - 원시값만 덜렁 반환하지 않는다
- **VO에는 domain을 포함할 수 없다** — 밖에서 domain이 필요한 일 자체가 없어야 함

---

## 🔑 주문 흐름은 어떻게 풀리는가

이 프로젝트의 핵심인 **주문(order-01)** 은 재고 · 포인트 · 쿠폰 · 주문이 한 트랜잭션에서
함께 움직입니다. order service는 필요한 도메인의 **port를 호출하고 VO를 받습니다.**

```
OrderService.create()
   ├─ CouponPort.use(...)           → DiscountResult   (소유·사용여부 검증은 coupon 안에서)
   ├─ OptionPort.decreaseStock(...) → StockResult      (재고 부족 판단은 option 안에서)
   ├─ UserPort.usePoint(...)        → PointResult      (포인트 부족 판단은 user 안에서)
   └─ (동일 옵션 합산 · 주문 금액 계산 · 주문 저장)  ← order 자신의 로직만 남음
```

order는 **재고가 충분한지 · 쿠폰이 유효한지 스스로 판단하지 않습니다.** 물어보고 결과를 받을
뿐입니다. `Option` · `Coupon` · `User`가 order 쪽으로 넘어오지 않으므로, order에는 그 판단을
직접 할 재료가 없습니다.

어느 단계가 실패해도 트랜잭션 경계에서 **전체가 롤백**됩니다.
주문 취소(order-04)도 동일하게 각 port의 롤백 행동(쿠폰 복원 · 포인트 환불 · 재고 복원)을
순서대로 호출합니다.

---

## ⚖️ 장점 · 단점

> **도입 이유** — 도메인 간 호출 가능한 표면을 타입으로 제한하기 위함.

### 장점
- **우회로가 없다** — controller용 함수는 다른 도메인의 시야에 아예 들어오지 않음.
  캡슐화를 지키는 것이 선택이 아니라 컴파일 조건
- **도메인 로직이 새어 나가지 않는다** — domain이 경계를 넘지 않으므로, 다른 도메인이
  getter로 로직을 재구성할 방법 자체가 없음
- **계약이 곧 문서** — `ProductPort`만 읽으면 "다른 도메인이 product에게 시킬 수 있는 일"의
  전부를 알 수 있음
- **테스트가 쉽다** — 의존이 전부 interface라 port를 mock으로 갈아끼우면 도메인 단독 테스트 가능
- **조율자가 필요 없다** — 도메인끼리 직접 호출하면서도 경계가 유지됨

### 단점
- **클래스 수 증가** — 도메인마다 UseCase + DomainPort + Service + VO들이 생김.
  단순 CRUD에서는 순수한 오버헤드
- **port 설계 난이도** — "충분한 반환값"의 기준이 주관적.
  잘못 자르면 port가 잘게 쪼개져 왕복 호출이 늘어남

---

## 🛠️ 기술 스택

- Java 21 · Spring Boot 3.3.0 (Web / Data JPA / Validation) · MySQL · Lombok · Gradle

> 현재 이 폴더는 설계 문서 단계이며, 구현 코드는 위 구조를 따라 추가될 예정입니다.
