package com.example.shop.domain.product.dto.response;

import com.example.shop.domain.product.entity.ProductInfo;
import com.example.shop.domain.user.dto.response.Creator;

/**
 * 상품 등록/수정/목록 응답 (옵션 미포함).
 * creator 이름은 product domain 이 모르므로 facade 가 user domain 에서 받아 채운다.
 */
public record ProductResponse(
        Long id,
        String description,
        int price,
        Creator creator
) {}
