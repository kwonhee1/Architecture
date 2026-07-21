package com.example.shop.domain.order.dto;

import com.example.shop.domain.user.service.vo.UserVo.UserInfo;

/**
 * order-03 응답의 구매자 { email, name }.
 * <p>
 * UserInfo 는 point 까지 실어 나르는 port 용 VO 라 응답에 그대로 쓸 수 없다.
 * 판매자에게 내보낼 필드만 여기서 골라 담는다.
 */
public record BuyerResponse(String email, String name) {

    public static BuyerResponse from(UserInfo info) {
        return new BuyerResponse(info.email(), info.name());
    }
}
