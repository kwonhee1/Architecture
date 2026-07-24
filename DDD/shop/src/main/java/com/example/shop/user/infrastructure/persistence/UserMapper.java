package com.example.shop.user.infrastructure.persistence;

import com.example.shop.user.domain.model.User;
import com.example.shop.user.domain.model.vo.Email;
import com.example.shop.user.domain.model.vo.Password;
import com.example.shop.user.domain.model.vo.Point;
import com.example.shop.user.domain.model.vo.UserId;

/** domain User <-> UserJpaEntity 변환. domain 은 JPA 를, JPA 는 VO 규칙을 모른다. */
final class UserMapper {

    private UserMapper() {
    }

    static UserJpaEntity toEntity(User user) {
        Long id = user.id() == null ? null : user.id().value();
        return new UserJpaEntity(
                id,
                user.email().value(),
                user.password().value(),
                user.name(),
                user.point().value()
        );
    }

    static User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                UserId.of(entity.getId()),
                Email.of(entity.getEmail()),
                Password.of(entity.getPassword()),
                entity.getName(),
                Point.of(entity.getPoint())
        );
    }
}
