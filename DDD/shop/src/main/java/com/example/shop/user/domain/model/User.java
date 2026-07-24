package com.example.shop.user.domain.model;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.user.domain.model.vo.Email;
import com.example.shop.user.domain.model.vo.Password;
import com.example.shop.user.domain.model.vo.Point;
import com.example.shop.user.domain.model.vo.UserId;

/**
 * user aggregate root.
 *
 * <p>자기 상태의 불변식을 스스로 지킨다. 규칙(포인트 충전·차감, 비밀번호 대조)은 Service 가 아니라
 * 이 aggregate 안에 있다. framework · JPA · 외부 DTO 를 알지 못한다.</p>
 *
 * <p>식별자는 저장 시점에 부여되므로 신규 생성({@link #register})은 id 가 없고,
 * 영속 상태 복원({@link #reconstitute})은 id 를 가진다.</p>
 */
public class User {

    private final UserId id;      // 신규 생성 시 null, 복원 시 존재
    private final Email email;
    private final Password password;
    private final String name;
    private Point point;

    private User(UserId id, Email email, Password password, String name, Point point) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 비어 있을 수 없습니다.");
        }
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.point = point;
    }

    /** user-01 : 신규 회원 생성. 포인트 0 으로 시작한다. */
    public static User register(Email email, Password password, String name) {
        return new User(null, email, password, name, Point.zero());
    }

    /** 영속 상태로부터 복원 (infrastructure 전용). */
    public static User reconstitute(UserId id, Email email, Password password, String name, Point point) {
        return new User(id, email, password, name, point);
    }

    /**
     * user-02 : 로그인 자격 증명 대조.
     * email 과 password 가 모두 일치해야 한다. 어느 쪽이 틀렸는지는 구분해서 알리지 않는다.
     */
    public void authenticate(Email email, Password password) {
        if (!this.email.equals(email) || !this.password.matches(password)) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
    }

    /** user-04 : 포인트 충전 (충전 포인트 > 0 은 Point 가 보장). */
    public void chargePoint(long amount) {
        this.point = this.point.charge(amount);
    }

    /** 주문 시 포인트 차감 (부족하면 실패). */
    public void usePoint(long amount) {
        this.point = this.point.use(amount);
    }

    /** 주문 취소 시 포인트 환불. */
    public void refundPoint(long amount) {
        this.point = this.point.refund(amount);
    }

    public UserId id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public Password password() {
        return password;
    }

    public String name() {
        return name;
    }

    public Point point() {
        return point;
    }
}
