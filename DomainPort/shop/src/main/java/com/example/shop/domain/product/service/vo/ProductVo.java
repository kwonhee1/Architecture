package com.example.shop.domain.product.service.vo;

import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.user.service.vo.UserVo.UserInfo;

/**
 * product 가 port 경계 밖으로 내보내는 VO 모음. Product entity 를 담지 않는다.
 * <p>
 * 여기 있는 타입은 port 의 반환값 전용이다. controller 로 나가는 응답 모양은 dto 가 따로 맡는다
 * ({@link ProductInfo} → ProductResponse).
 */
public final class ProductVo {

    private ProductVo() {}

    /** creatorName 은 product 가 UserDomainPort 에 물어 채운다 (User entity 를 알지 못한다) */
    public record ProductInfo(Long id, String description, int price, String creatorName) {
        /** 생성자는 Product 안에 없으므로(다른 domain 의 값) UserDomainPort 가 준 VO 를 받아 채운다 */
        public static ProductInfo of(Product product, UserInfo creator) {
            return new ProductInfo(
                    product.getId(),
                    product.getDescription(),
                    product.getPrice(),
                    creator != null ? creator.name() : null);
        }
    }
}

