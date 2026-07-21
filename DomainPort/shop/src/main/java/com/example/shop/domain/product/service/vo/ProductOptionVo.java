package com.example.shop.domain.product.service.vo;

import com.example.shop.domain.product.entity.ProductOption;

/**
 * option 이 port 경계 밖으로 내보내는 VO 모음. ProductOption / Product entity 를 담지 않는다.
 * <p>
 * 여기 있는 타입은 port 의 반환값 전용이다. controller 로 나가는 응답 모양은 dto 가 따로 맡는다
 * ({@link OptionInfo} → ProductOptionResponse).
 */
public final class ProductOptionVo {

    private ProductOptionVo() {}

    public record OptionInfo(Long id, String description, int additionalPrice, int stock) {
        public static OptionInfo of(ProductOption option) {
            return new OptionInfo(
                    option.getId(),
                    option.getDescription(),
                    option.getAdditionalPrice(),
                    option.getStock());
        }
    }

    /**
     * 재고 차감 결과.
     * order 는 단가를 스스로 계산하지 않고 여기 실린 unitPrice / purchaseAmount 를 받아 쓴다.
     */
    public record StockDecreaseResult(
            OptionInfo option,
            ProductVo.ProductInfo product,
            int count,
            // int unitPrice,
            int purchaseAmount,
            int remainingStock
    ) {}

    public record StockRestoreResult(
            ProductVo.ProductInfo product,
            OptionInfo option,
            int restoredCount,
            int remainingStock
    ) {}
}
