문제점 : service함수들에서 "controller용 public"과 "다른 도메인용 public"이 시그니처상 구분이 안 됨
때문에 다른 domain에서 controller용 public 함수를 호출하여 domain 을 그대로 가져가 버림 

해결 방법 : controller용 기능 명세, 다른 domain용 기능 명세를 나누어 interface로 정의한다 (use case와 비슷함)

컨트롤러용 UseCase + 타 도메인용 DomainPort, 두 계약으로 서비스 표면을 분리한다

controller <> service : Usecase, controller가 사용할 수 있는 service 함수들 명세
타 domain <> service : DomainPort, 다른 domain에서 사용 가능한 service함수들 명세

규칙
  service
    - 다른 domain service 사용 불가, domain port 사용 가능
    - 다른 domain 의 로직은 무조건 domain port를 걸쳐서 사용한다 (다른 domain의 함수를 domain에서 직접 호출하지 않는다)

  port
    - 반환할 때 vo를 사용한다
    - 호출에 따른 output이 아닌 "행동"에 따른 충분한 반환값을 반환한다

추가 설명
  클린 아키텍쳐의 port 원칙 3개
      1. 원시 결과값 — 판단/실행형
boolean isCreator(...), void applyDiscount(...), Long createAndReturnId(...). B가 A의 로직을 실행시키고 그 결과만 받는 경우. 재료가 안 나가서 가장 안전합니다.
      2. 경계 VO(Info) — 조회형
ProductInfo findById(...). B가 A의 데이터를 조합해야 할 때. domain을 직접 반환하는 대신 읽기 전용 VO로 감싸서 냅니다. 이게 없으면 "product 이름·가격을 가져와 내 응답에 넣는" 유스케이스를 못 합니다. 아까 님이 걱정한 "domain 직접 반환"의 안전한 대체물이 바로 이겁니다.
      3. 결과 VO — 실행 결과가 여러 값일 때
PurchaseResult purchase(...). 실행 결과가 boolean 하나로 안 끝나고 여러 필드일 때. 이것도 VO입니다.

  받을 port 방식 : 결과형 
    domain port는 domain 로직을 실행하기 위한 창구
    파라미터는 충분한 만큽, 반환값을 행동을 충분히 설명할 만큼 제공한다
    output : vo (반환을 원시값만 반환하지 않는다)
    vo에는 doamin을 포함할 수 없다! (외부에서 domain이 필요한 일이 없어야함)
