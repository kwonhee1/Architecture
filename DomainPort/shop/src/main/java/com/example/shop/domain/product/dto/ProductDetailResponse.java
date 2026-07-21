package com.example.shop.domain.product.dto;

import com.example.shop.domain.user.dto.CreatorResponse;

import java.util.List;

/**
 * product-04 상세 응답.
 * 상품 정보 + 옵션 리스트가 한 단계로 펼쳐진 모양이라 ProductResponse 를 그대로 쓰지 못하고
 * 이 dto 만 따로 둔다.
 */
public record ProductDetailResponse(
        Long id,
        String description,
        int price,
        CreatorResponse creator,
        List<ProductOptionResponse> options
) {}
