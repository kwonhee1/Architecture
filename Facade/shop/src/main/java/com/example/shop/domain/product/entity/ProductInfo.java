package com.example.shop.domain.product.entity;

import com.example.shop.domain.product.dto.response.ProductDetailResponse;
import com.example.shop.domain.product.dto.response.ProductResponse;
import com.example.shop.domain.user.entity.UserInfo;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * product domain 이 외부(facade)에 노출하는 조회용 VO.
 * creator 는 다른 domain 이므로 id 만 갖고 있고, 이름 채우기는 facade 가 user domain 에 물어본다.
 */
@NoArgsConstructor
public class ProductInfo{

    private Long id;
    private String description;
    private int price;
    private Long creatorId;

    public Long getId() {
        return id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public static ProductInfo from(Product product) {
        ProductInfo info = new ProductInfo();

        info.id = product.getId();
        info.description = product.getDescription();
        info.price = product.getPrice();
        info.creatorId = product.getCreatorId();

        return info;
    }

    public ProductResponse toProductResponse(UserInfo creator) {
        return new ProductResponse(id, description, price, creator.toCreator());
    }

    /** product-04 : 상품 + creator + 옵션 리스트를 상세 응답으로 만든다 */
    public ProductDetailResponse toProductDetailResponse(UserInfo creator, List<OptionInfo> options) {
        return new ProductDetailResponse(id, description, price, creator.toCreator(), options);
    }

    /** 상품 목록에 creator 를 id 로 짝지어 응답으로 만든다 */
    public static List<ProductResponse> toProductResponses(List<ProductInfo> products,
                                                           List<UserInfo> creators) {
        Map<Long, UserInfo> creatorsById = creators.stream()
                .collect(Collectors.toMap(UserInfo::getId, Function.identity()));

        return products.stream()
                .map(product -> product.toProductResponse(creatorsById.get(product.creatorId)))
                .toList();
    }
}
