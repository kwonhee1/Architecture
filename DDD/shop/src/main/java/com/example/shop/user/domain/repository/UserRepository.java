package com.example.shop.user.domain.repository;

import com.example.shop.user.domain.model.User;
import com.example.shop.user.domain.model.vo.Email;
import com.example.shop.user.domain.model.vo.UserId;

import java.util.Optional;

/**
 * user aggregate 저장/조회 계약.
 * interface 는 domain 에, 구현은 infrastructure 에 둔다 (의존성 역전).
 * domain 객체(User)와 VO 만 주고받는다.
 */
public interface UserRepository {

    /** 저장 후 식별자가 부여된 User 를 반환한다. */
    User save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
