package com.example.shop.domain.product.service.port;

import com.example.shop.domain.product.service.vo.ProductOptionVo.*;

import java.util.List;

/**
 * 타 domain ↔ option service 계약.
 * <p>
 * order 는 재고가 충분한지 스스로 판단하지 않는다. decreaseStock 을 호출하고
 * 결과 VO 가 설명해 주는 것(단가·주문 금액·남은 재고)만 받아 간다.
 */
public interface ProductOptionDomainPort {

    /** 옵션 정보 일괄 조회 (order-02 응답의 옵션 정보 용) */
    List<OptionInfo> getOptionInfos(List<Long> optionIds);

    /** 재고 차감. 재고 부족 판단은 option 이 한다. 실패 시 예외 → 호출 트랜잭션 롤백. */
    StockDecreaseResult decreaseStock(Long optionId, int count);

    /** 주문 취소 시 재고 복원 */
    StockRestoreResult restoreStock(Long optionId, int count);
}
