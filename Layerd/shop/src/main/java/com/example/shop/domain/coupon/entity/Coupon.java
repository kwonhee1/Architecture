package com.example.shop.domain.coupon.entity;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    /** 금액 할인 (% 아님), > 0 */
    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private boolean used;

    @Builder
    public Coupon(User user, String name, int discountAmount) {
        this.user = user;
        this.name = name;
        this.discountAmount = discountAmount;
        this.used = false;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    /** 주문 시 쿠폰 사용 (본인 쿠폰만, 미사용 상태만) → 할인 금액 반환 */
    public int use(User user) {
        if (!this.user.getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NOT_COUPON_OWNER);
        }
        if (this.used) {
            throw new BusinessException(ErrorCode.COUPON_ALREADY_USED);
        }
        this.used = true;
        return this.discountAmount;
    }

    /** 주문 취소 시 쿠폰 복구 */
    public void restore() {
        this.used = false;
    }
}
