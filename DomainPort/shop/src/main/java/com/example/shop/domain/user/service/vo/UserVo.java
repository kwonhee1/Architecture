package com.example.shop.domain.user.service.vo;

import com.example.shop.domain.user.entity.User;

/**
 * user domain 이 port 경계 밖으로 내보내는 VO 모음.
 * <p>
 * 어떤 VO 도 User entity(domain) 를 담지 않는다. 바깥에서 domain 이 필요한 일이 없어야 하므로,
 * 필요한 값만 골라 읽기 전용으로 복사해 내보낸다.
 * <p>
 * port 로 들어가는 파라미터는 VO 로 감싸지 않는다. 나가는 값만 VO 다.
 */
public final class UserVo {

    private UserVo() {}

    /**
     * 다른 domain 이 보게 되는 사용자 정보 (password 는 내보내지 않는다).
     * 여러 건을 한 번에 받아 갈 때 어느 사용자인지 가려낼 수 있도록 id 를 싣는다.
     */
    public record UserInfo(Long id, String email, String name, long point) {
        public static UserInfo of(User user) {
            return new UserInfo(user.getId(), user.getEmail(), user.getName(), user.getPoint());
        }
    }

    public record PointUseResult(UserInfo user, long usedAmount, long remainingPoint) {}

    public record PointRefundResult(UserInfo user, long refundedAmount, long remainingPoint) {}
}
