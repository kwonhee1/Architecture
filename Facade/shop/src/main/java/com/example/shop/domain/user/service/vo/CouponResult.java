package com.example.shop.domain.user.service.vo;

import com.example.shop.domain.user.entity.Coupon;
import com.example.shop.domain.user.entity.CouponInfo;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.entity.UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CouponResult {

    private UserInfo user;
    private CouponInfo couponInfo;
    private int amount;

    public static CouponResult from(User user, Coupon coupon, int amount) {
        CouponResult result = new CouponResult();
        result.couponInfo = CouponInfo.from(coupon);
        result.user = UserInfo.from(user);
        result.amount = amount;
        return result;
    }

    public static CouponResult none(User user) {
        CouponResult result = new CouponResult();
        result.couponInfo = null;
        result.user = UserInfo.from(user);
        result.amount = 0;
        return result;
    }
}
