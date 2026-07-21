package com.example.shop.domain.user.service.port;

import com.example.shop.domain.user.service.vo.UserVo.*;

import java.util.List;

/**
 * 타 domain ↔ user service 계약.
 * <p>
 * 다른 domain 은 UserService 가 아니라 이 port 만 주입받는다.
 * 반환값은 그 행동을 설명할 만큼의 VO 이며, User entity 는 이 경계를 넘지 않는다.
 */
public interface UserDomainPort {

    /** 사용자 정보 조회 (product 의 생성자 정보, order 의 주문자/구매자 정보 용) */
    UserInfo getUserInfo(Long userId);

    /** 사용자 정보 일괄 조회 (order-03 구매자 목록 용) */
    List<UserInfo> getUserInfos(List<Long> userIds);

    /** 포인트 차감. 부족하면 실패로 예외를 던져 호출한 트랜잭션을 롤백시킨다. */
    PointUseResult usePoint(Long userId, long amount);

    /** 주문 취소 시 포인트 환불 */
    PointRefundResult refundPoint(Long userId, long amount);
}
