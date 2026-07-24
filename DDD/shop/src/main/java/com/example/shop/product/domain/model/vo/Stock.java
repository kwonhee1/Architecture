package com.example.shop.product.domain.model.vo;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;

/**
 * 재고 값 객체. 불변이며 값으로 비교한다.
 * "재고는 0 이상", "재고보다 많이 뺄 수 없다" 는 불변식을 스스로 지킨다.
 */
public record Stock(long value) {

    public Stock {
        if (value < 0) {
            throw new BusinessException(ErrorCode.INVALID_OPTION_STOCK);
        }
    }

    public static Stock of(long value) {
        return new Stock(value);
    }

    /** 주문 시 재고 차감 (부족하면 실패). */
    public Stock decrease(long quantity) {
        if (this.value < quantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
        return new Stock(this.value - quantity);
    }

    /** 주문 취소 시 재고 복원. */
    public Stock increase(long quantity) {
        return new Stock(this.value + quantity);
    }
}
