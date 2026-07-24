# DDD architecture

presentation -> application -> domain <- infrastructure

> 모든 의존성은 domain을 향한다. domain은 어떤 것에도 의존하지 않는다.

## 다른 architecture 와의 차이점

1. domain model / anemic model

> domain은 데이터 구조가 아니라 규칙의 주인이다
> 데이터를 가진 쪽이 그 데이터의 규칙도 가진다

일반적인 layered architecture에서 entity는 getter/setter만 가진 데이터 덩어리이고, 모든 규칙은 service에 쌓인다. DDD는 규칙을 데이터가 있는 곳(entity / value object)으로 되돌린다.

    - domain layer

    business 규칙과 불변식(invariant)을 소유한다
    framework, DB, 외부 API를 알지 못한다
    entity / value object / aggregate / domain service / repository interface 로 구성된다

    - application layer

    use case의 흐름을 조립한다 (실행 순서, transaction 범위, 권한 확인, 외부 호출)
    business 규칙을 판단하지 않는다
    domain 객체에게 시키고, 결과를 저장하고, 반환할 뿐이다

    - infrastructure layer

    repository interface의 구현, ORM, 외부 API, 메시지 발행을 담당한다
    domain이 정의한 계약(interface)을 구현하는 쪽이다 (의존성 역전)

    - presentation layer

    요청 / 응답의 변환만 담당한다
    domain 객체를 그대로 노출하지 않는다

2. aggregate

> 일관성을 함께 지켜야 하는 객체들의 묶음

aggregate는 transaction 경계이자 불변식의 단위다. 외부는 aggregate root만 알고, root를 통해서만 내부를 조작한다. 이 경계가 없으면 "어디까지가 한 번에 지켜져야 하는 규칙인가"를 아무도 대답할 수 없다.

3. bounded context

> 같은 단어가 context마다 다른 의미를 가진다

`Order`의 `User`와 `Payment`의 `User`는 다른 모델이다. 하나의 거대한 공용 모델을 만들지 않고, context마다 자기 모델을 갖는다. context 사이는 통합 계약(전용 모델 / 이벤트)으로 연결한다.

## 규칙

### domain

1. domain layer는 다른 layer에 의존하지 않는다. framework annotation, DB 스키마, 외부 DTO가 domain에 들어오지 않는다.
2. 도메인 로직은 entity / value object에 우선 배치한다. 한 객체에 담기지 않는 규칙(여러 aggregate가 협력해야 하는 계산·판정)만 domain service로 뺀다.
3. entity는 항상 유효한 상태로만 존재한다. 생성 시점에 불변식을 검증하고, 무의미한 setter를 열지 않는다.
4. 값에 정체성이 없다면 value object로 만든다. value object는 불변이며 값으로 비교한다.
   (`String email` = X, `Email email` = O)
5. 원시값을 그대로 다루지 않는다. 의미 있는 값은 타입으로 표현한다.
   (`Long amount` = X, `Money amount` = O)

### aggregate

6. 외부는 aggregate root를 통해서만 aggregate 내부에 접근한다. 내부 entity를 밖으로 반환하거나 직접 수정하지 않는다.
7. aggregate 간 참조는 객체가 아니라 식별자(ID)로 한다.
   (`Order.user: User` = X, `Order.userId: UserId` = O)
8. aggregate는 작게 유지한다. "함께 지켜져야 하는 규칙이 있는가"만이 aggregate에 포함시킬 근거다. 조회 편의는 근거가 아니다.
9. 여러 aggregate가 함께 성공/실패해야 하는 use case(주문, 가입→쿠폰, 상품→기본옵션)는 하나의 transaction에서 조율한다. 규칙 판정은 각 aggregate가 하고, application service는 순서와 transaction 경계만 관리하며 실패 시 롤백으로 원자성을 지킨다. 이 구현은 domain event / 결과적 일관성을 사용하지 않는다.

### repository

10. repository는 aggregate root 단위로만 만든다. 내부 entity 전용 repository를 만들지 않는다.
11. repository interface는 domain에, 구현은 infrastructure에 둔다.
12. repository는 domain 객체를 주고받는다. 조회 전용 화면 데이터가 필요하면 repository를 비틀지 말고 별도의 조회 모델(query model)을 둔다.

### application

13. application service는 domain 로직을 판단하지 않는다. 분기·계산·검증이 늘어난다면 그 규칙이 domain으로 가야 한다는 신호다.
14. transaction의 범위는 application service에서 관리한다.
15. application service는 다른 application service를 호출하지 않는다. 공통 흐름이 필요하면 domain 또는 별도 협력 객체로 내린다.
16. presentation으로는 domain 객체가 아니라 DTO를 반환한다. 이 DTO의 이름은 `{도메인}Info`로 통일한다.
    (`UserInfo`, `ProductInfo`, `OrderInfo`, `CouponInfo`)

## aggregate 경계를 넘는 변경

여러 aggregate가 함께 변해야 하는 흐름은 domain event로 쪼개지 않고, application service가 하나의 transaction으로 조율한다.

- 규칙 판정은 각 aggregate가 소유한다 (재고=Option, 포인트=User/Point, 쿠폰=Coupon, 금액=Order)
- application service는 판단하지 않고, 각 aggregate에게 시키는 순서를 세우고 transaction 경계만 관리한다
- 어느 단계라도 실패하면 예외가 전파되어 transaction 전체가 롤백된다 (원자성)

> 이유: 이 도메인의 주문/취소는 재고·포인트·쿠폰·주문이 원자적으로 함께 움직여야 한다. 이를 event로 나누면 보상 transaction·재처리가 따라오는데, 이 규모에서는 이득 없이 복잡도만 커진다. 그래서 "한 transaction = 한 aggregate"를 의도적으로 완화했다.

## bounded context 간 통합

- context 간에는 domain 객체를 공유하지 않는다. 각자의 모델로 번역해서 받는다
- 다른 context의 데이터/판정이 필요하면 상대가 공개한 전용 계약(port interface)만 사용한다. 계약의 입출력은 원시값 또는 VO로 하고, domain을 노출하지 않는다 (예: `OrderExistencePort`)
