package com.example.shop.order.application;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.coupon.domain.model.Coupon;
import com.example.shop.coupon.domain.model.vo.CouponId;
import com.example.shop.coupon.domain.repository.CouponRepository;
import com.example.shop.order.domain.model.Order;
import com.example.shop.order.domain.model.OrderLine;
import com.example.shop.order.domain.model.vo.BuyerId;
import com.example.shop.order.domain.model.vo.OrderId;
import com.example.shop.order.domain.repository.OrderRepository;
import com.example.shop.product.domain.model.Option;
import com.example.shop.product.domain.model.vo.OptionId;
import com.example.shop.product.domain.repository.OptionRepository;
import com.example.shop.user.domain.model.User;
import com.example.shop.user.domain.model.vo.UserId;
import com.example.shop.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * order-04 : 주문 취소.
 *
 * <p>재고 · 쿠폰 · 포인트를 되돌리고 주문을 삭제한다. order-01 과 마찬가지로 여러 aggregate 를
 * 한 트랜잭션에서 원자적으로 복원한다. 내가 주문한 주문만 취소할 수 있다.</p>
 */
@Service
@RequiredArgsConstructor
public class CancelOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final OptionRepository optionRepository;

    @Transactional
    public void cancel(long requesterId, long orderId) {
        Order order = orderRepository.findById(OrderId.of(orderId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.validateOwner(BuyerId.of(requesterId));

        // 재고 복원
        for (OrderLine line : order.lines()) {
            Option option = optionRepository.findById(OptionId.of(line.optionId()))
                    .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
            option.increaseStock(line.quantity());
            optionRepository.save(option);
        }

        // 쿠폰 사용 가능 상태로 복원
        if (order.hasCoupon()) {
            Coupon coupon = couponRepository.findById(CouponId.of(order.couponId()))
                    .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
            coupon.restore();
            couponRepository.save(coupon);
        }

        // 포인트 환불
        User buyer = userRepository.findById(UserId.of(requesterId))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        buyer.refundPoint(order.amount());
        userRepository.save(buyer);

        orderRepository.delete(order);
    }
}
