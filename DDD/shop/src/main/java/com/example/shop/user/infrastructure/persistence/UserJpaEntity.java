package com.example.shop.user.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DB 스키마 전용 엔티티 (도메인 아님).
 * domain 의 User 와 분리되어 있으며, 매핑은 {@link UserMapper} 가 담당한다.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity {

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

    public UserJpaEntity(Long id, String email, String password, String name, long point) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.point = point;
    }
}
