package com.example.shop.order.application;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.coupon.domain.model.Coupon;
import com.example.shop.coupon.domain.model.vo.CouponId;
import com.example.shop.coupon.domain.model.vo.OwnerId;
import com.example.shop.coupon.domain.repository.CouponRepository;
import com.example.shop.order.application.dto.OrderLineCommand;
import com.example.shop.order.application.dto.OrderInfo;
import com.example.shop.order.application.dto.PlaceOrderCommand;
import com.example.shop.order.domain.model.Order;
import com.example.shop.order.domain.model.OrderLine;
import com.example.shop.order.domain.model.vo.BuyerId;
import com.example.shop.order.domain.repository.OrderRepository;
import com.example.shop.product.domain.model.Option;
import com.example.shop.product.domain.model.Product;
import com.example.shop.product.domain.model.vo.OptionId;
import com.example.shop.product.domain.model.vo.ProductId;
import com.example.shop.product.domain.repository.OptionRepository;
import com.example.shop.product.domain.repository.ProductRepository;
import com.example.shop.user.domain.model.User;
import com.example.shop.user.domain.model.vo.UserId;
import com.example.shop.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * order-01 : 주문.
 *
 * <p>여러 aggregate(coupon · option · user · order)가 한 흐름에서 함께 움직인다. 이 도메인은
 * "어느 한 단계라도 실패하면 전체 취소" 라는 원자성을 요구하므로, "한 트랜잭션 = 한 aggregate"
 * 원칙을 이 use case 에서는 의도적으로 완화하고 <b>하나의 트랜잭션</b>으로 조율한다. 각 규칙(쿠폰
 * 사용 가능 여부, 재고, 포인트, 금액 계산)은 여전히 각 aggregate 가 스스로 판정하며, application 은
 * 순서를 세우고 실패 시 트랜잭션 롤백으로 원자성을 보장할 뿐이다.</p>
 */
@Service
@RequiredArgsConstructor
public class PlaceOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;

    @Transactional
    public OrderInfo place(long buyerId, PlaceOrderCommand command) {
        // 동일 optionId 는 개수를 합산 (입력 순서 유지)
        Map<Long, Integer> merged = new LinkedHashMap<>();
        for (OrderLineCommand item : command.items()) {
            merged.merge(item.optionId(), item.count(), Integer::sum);
        }

        // 재고 차감 + 단가 스냅샷으로 주문 라인 구성
        List<OrderLine> lines = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : merged.entrySet()) {
            long optionId = entry.getKey();
            int count = entry.getValue();
            Option option = optionRepository.findById(OptionId.of(optionId))
                    .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
            Product product = productRepository.findById(option.productId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            long unitPrice = option.unitPrice(product.price());
            option.decreaseStock(count);                 // 재고 부족 시 예외 → 롤백
            optionRepository.save(option);

            lines.add(OrderLine.of(product.id().value(), optionId, count, unitPrice));
        }

        // 쿠폰 사용 (선택) → 할인 금액
        long discount = 0L;
        if (command.couponId() != null) {
            Coupon coupon = couponRepository.findById(CouponId.of(command.couponId()))
                    .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
            discount = coupon.use(OwnerId.of(buyerId)).value();  // 소유/사용여부 검증 → 예외 시 롤백
            couponRepository.save(coupon);
        }

        Order order = Order.place(BuyerId.of(buyerId), command.couponId(), lines, discount);

        // 포인트 차감 (쿠폰 적용 후 금액)
        User buyer = userRepository.findById(UserId.of(buyerId))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        buyer.usePoint(order.amount());                  // 포인트 부족 시 예외 → 롤백
        userRepository.save(buyer);

        Order saved = orderRepository.save(order);

        List<OrderInfo.Line> infoLines = saved.lines().stream()
                .map(l -> new OrderInfo.Line(l.optionId(), l.quantity()))
                .toList();
        return new OrderInfo(saved.id().value(), infoLines, saved.amount(),
                saved.orderDate(), buyer.name());
    }
}
