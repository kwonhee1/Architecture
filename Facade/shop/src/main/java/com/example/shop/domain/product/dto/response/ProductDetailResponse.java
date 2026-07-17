package com.example.shop.domain.product.dto.response;

import com.example.shop.domain.product.entity.OptionInfo;
import com.example.shop.domain.product.entity.ProductInfo;
import com.example.shop.domain.user.dto.response.Creator;

import java.util.List;

/** product-04 상세 응답 (옵션 리스트 포함) */
public record ProductDetailResponse(
        Long id,
        String description,
        int price,
        Creator creator,
        List<OptionInfo> options
) {}
