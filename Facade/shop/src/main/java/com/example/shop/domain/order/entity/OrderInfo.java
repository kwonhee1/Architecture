package com.example.shop.domain.order.entity;

import com.example.shop.domain.order.dto.response.BuyerInfo;
import com.example.shop.domain.order.dto.response.BuyerOrderLineDetail;
import com.example.shop.domain.order.dto.response.BuyerOrderResponse;
import com.example.shop.domain.order.dto.response.OrderResult;
import com.example.shop.domain.order.dto.response.SellerOptionOrders;
import com.example.shop.domain.order.dto.response.SellerOrderEntry;
import com.example.shop.domain.product.dto.response.ProductResponse;
import com.example.shop.domain.product.entity.OptionInfo;
import com.example.shop.domain.product.entity.ProductInfo;
import com.example.shop.domain.user.dto.response.UserSummary;
import com.example.shop.domain.user.entity.CouponInfo;
import com.example.shop.domain.user.entity.UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * order domain 이 외부(facade)에 노출하는 주문 조회용 VO.
 * user / coupon / product / option 은 다른 domain 이므로 id 만 갖고 있고,
 * facade 가 그 id 로 채워온 값을 응답으로 조립하는 일은 이 VO 가 한다.
 */
@NoArgsConstructor
@Getter
public class OrderInfo {

    private Long id;
    private Long userId;
    private Long couponId;
    private List<OrderItemInfo> items;
    private int amount;
    private LocalDate orderDate;

    public static OrderInfo from(Order order) {
        OrderInfo info = new OrderInfo();

        info.id = order.getId();
        info.userId = order.getUserId();
        info.couponId = order.getCouponId();
        info.items = order.getItems().stream().map(OrderItemInfo::from).toList();
        info.amount = order.getAmount();
        info.orderDate = order.getOrderDate();

        return info;
    }

    /** order-01 : 주문 + 주문자를 응답으로 만든다 */
    public OrderResult toOrderResult(UserInfo orderer) {
        return new OrderResult(
                id,
                items.stream().map(OrderItemInfo::toOrderItemResponse).toList(),
                amount,
                orderDate,
                orderer.toCreator()
        );
    }

    /**
     * order-02 : 주문 리스트 + 채워온 상품/옵션/쿠폰으로 구매자 응답을 만든다.
     * 상품 응답에 creator 이름을 채우는 방법은 product domain 의 규칙이므로 그쪽에 맡긴다.
     */
    public static List<BuyerOrderResponse> toBuyerOrderResponses(List<OrderInfo> orders,
                                                                 List<ProductInfo> products,
                                                                 List<UserInfo> creators,
                                                                 List<OptionInfo> options,
                                                                 List<CouponInfo> coupons) {
        Map<Long, ProductResponse> productsById = ProductInfo.toProductResponses(products, creators).stream()
                .collect(Collectors.toMap(ProductResponse::id, Function.identity()));
        Map<Long, OptionInfo> optionsById = options.stream()
                .collect(Collectors.toMap(OptionInfo::id, Function.identity()));
        Map<Long, CouponInfo> couponsById = coupons.stream()
                .collect(Collectors.toMap(CouponInfo::id, Function.identity()));

        return orders.stream()
                .map(order -> new BuyerOrderResponse(
                        order.id,
                        order.items.stream()
                                .map(item -> new BuyerOrderLineDetail(
                                        productsById.get(item.getProductId()),
                                        optionsById.get(item.getOptionId()),
                                        item.getCount()))
                                .toList(),
                        order.amount,
                        order.orderDate,
                        couponsById.get(order.couponId)))
                .toList();
    }

    /**
     * order-03 : 해당 상품이 팔린 주문 줄만 골라 옵션 id 기준으로 묶는다.
     * 한 주문에는 다른 상품의 줄도 섞여 있으므로 productId 로 걸러낸다.
     */
    public static List<SellerOptionOrders> toSellerOptionOrders(List<OrderInfo> orders,
                                                                Long productId,
                                                                List<UserInfo> buyers) {
        Map<Long, UserSummary> buyersById = buyers.stream()
                .map(UserInfo::toUserSummary)
                .collect(Collectors.toMap(UserSummary::id, Function.identity()));

        Map<Long, List<SellerOrderEntry>> grouped = new LinkedHashMap<>();
        for (OrderInfo order : orders) {
            UserSummary buyer = buyersById.get(order.userId);
            for (OrderItemInfo item : order.items) {
                if (!item.getProductId().equals(productId)) {
                    continue;
                }
                grouped.computeIfAbsent(item.getOptionId(), key -> new ArrayList<>())
                        .add(new SellerOrderEntry(new BuyerInfo(buyer.email(), buyer.name()), item.getCount()));
            }
        }
        return grouped.entrySet().stream()
                .map(entry -> new SellerOptionOrders(entry.getKey(), entry.getValue()))
                .toList();
    }
}
