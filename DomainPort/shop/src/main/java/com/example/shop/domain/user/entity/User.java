package com.example.shop.domain.user.entity;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private long point;

    @Builder
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.point = 0L;
    }

    public boolean matchPassword(String rawPassword) {
        return this.password.equals(rawPassword);
    }

    /** user-04 : 포인트 충전 (충전 포인트 > 0) */
    public void addPoint(long amount) {
        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_CHARGE_POINT);
        }
        this.point += amount;
    }

    /** 주문 시 포인트 차감 (부족하면 실패) */
    public void usePoint(long amount) {
        if (this.point < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT);
        }
        this.point -= amount;
    }

    /** 주문 취소 시 포인트 환불 */
    public void refundPoint(long amount) {
        this.point += amount;
    }
}
