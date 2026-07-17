# facade architecture (기존 facade architecture와는 목적이 다릅니다)

controller -> facade service -> domain service

## 다른 architecture 와의 차이점

1. application service / domain service 
    
> domain 로직은 다른 domain 로직과 섞이거나 침범해서는 안된다
> domain service는 해당 domain 의 로직만 관리한다
    
service를 domain service와 application service로 나누고, domain service는 domain로직에만 집중하고 나머지 책임은 application service가 가져간다
    
    - domain service 
    
    각 domain에 해당하는 domain 로직만을 담당한다
    다른 domain의 domain service에 의존하지 않는다
    
    - application service
    
    실행 순서, transaction 범위, 외부 service 호출의 책임을 가진다
    domain 로직의 책임을 가지지 않는다
    
> A domain은 언제나 A domain 로직을 통해야 한다 (B가 A의 port를 직접 사용해서는 안된다)
    

## 규칙

1. domain service는 다른 domain의 service 호출 불가, facade service는 다른 facade service 호출 불가
2. application service에서 domain 로직을 처리하지 않는다, 외부 adapter를 직접 호출 할 수 있다 (domain 로직과 분리된 외부 adpater 호출은 facade에서 책임질 것을 권장합니다)
3. domain service에서 다른 domain을 파라미터로 받을 때는 Domain / VO class로 받는다
    (Long userId = X, User user = O, UserId userId = O)
4. transaction의 범위는 application service에서 관리한다

## 외부 adpater
해당 architecture에서는 adapter / port 개념을 직접적으로 다루지 않습니다 (필요에 맞게 알맞게 도입하세요)
domain 로직을 지키기 위한 만큼, 외부 port는 service와 분하여 facade에서 처리할 것을 권장합니다
