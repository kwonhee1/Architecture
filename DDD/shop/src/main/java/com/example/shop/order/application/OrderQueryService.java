package com.example.shop.order.application;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.coupon.domain.model.Coupon;
import com.example.shop.coupon.domain.model.vo.CouponId;
import com.example.shop.coupon.domain.repository.CouponRepository;
import com.example.shop.order.application.dto.BuyerOrderInfo;
import com.example.shop.order.application.dto.BuyerOrderInfo.*;
import com.example.shop.order.application.dto.SellerOrderInfo;
import com.example.shop.order.domain.model.Order;
import com.example.shop.order.domain.model.OrderLine;
import com.example.shop.order.domain.model.vo.BuyerId;
import com.example.shop.order.domain.repository.OrderRepository;
import com.example.shop.product.domain.model.Option;
import com.example.shop.product.domain.model.Product;
import com.example.shop.product.domain.model.vo.CreatorId;
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

/** order-02 / order-03 조회. 응답에 필요한 다른 aggregate 데이터를 읽어 view 로 조립한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;

    /** order-02 : 구매자 주문 리스트. */
    public List<BuyerOrderInfo> getMyOrders(long buyerId) {
        return orderRepository.findByBuyerId(BuyerId.of(buyerId)).stream()
                .map(this::toBuyerOrder)
                .toList();
    }

    /** order-03 : 판매자 주문 리스트 (상품 생성자만, 옵션별로 묶음). */
    public List<SellerOrderInfo> getProductOrders(long productId, long requesterId) {
        Product product = productRepository.findById(ProductId.of(productId))
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        product.validateOwner(CreatorId.of(requesterId));

        Map<Long, List<SellerOrderInfo.Entry>> grouped = new LinkedHashMap<>();
        for (Order order : orderRepository.findByProductId(productId)) {
            User buyer = loadUser(order.buyerId().value());
            SellerOrderInfo.BuyerView buyerView =
                    new SellerOrderInfo.BuyerView(buyer.email().value(), buyer.name());
            for (OrderLine line : order.lines()) {
                if (line.productId() != productId) {
                    continue;
                }
                grouped.computeIfAbsent(line.optionId(), k -> new ArrayList<>())
                        .add(new SellerOrderInfo.Entry(buyerView, line.quantity()));
            }
        }
        return grouped.entrySet().stream()
                .map(e -> new SellerOrderInfo(e.getKey(), e.getValue()))
                .toList();
    }

    private BuyerOrderInfo toBuyerOrder(Order order) {
        List<LineDetail> details = order.lines().stream()
                .map(this::toLineDetail)
                .toList();
        CouponView coupon = null;
        if (order.hasCoupon()) {
            coupon = couponRepository.findById(CouponId.of(order.couponId()))
                    .map(this::toCouponView)
                    .orElse(null);
        }
        return new BuyerOrderInfo(order.id().value(), details, order.amount(),
                order.orderDate(), coupon);
    }

    private LineDetail toLineDetail(OrderLine line) {
        Product product = productRepository.findById(ProductId.of(line.productId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        Option option = optionRepository.findById(OptionId.of(line.optionId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
        ProductView productView = new ProductView(
                product.id().value(), product.description(),
                product.price().value(), creatorName(product.creatorId()));
        OptionView optionView = new OptionView(
                option.id().value(), option.description(),
                option.additionalPrice(), option.stock());
        return new LineDetail(productView, optionView, line.quantity());
    }

    private CouponView toCouponView(Coupon coupon) {
        return new CouponView(coupon.id().value(), coupon.name(), coupon.discount().value());
    }

    private String creatorName(CreatorId creatorId) {
        return userRepository.findById(UserId.of(creatorId.value()))
                .map(User::name)
                .orElse(null);
    }

    private User loadUser(long userId) {
        return userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
