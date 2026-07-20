package com.example.shop.domain.product.facade;

import com.example.shop.domain.order.service.OrderService;
import com.example.shop.domain.product.dto.request.ProductCreateRequest;
import com.example.shop.domain.product.dto.request.ProductUpdateRequest;
import com.example.shop.domain.product.dto.response.ProductDetailResponse;
import com.example.shop.domain.product.dto.response.ProductResponse;
import com.example.shop.domain.product.entity.OptionInfo;
import com.example.shop.domain.product.entity.ProductInfo;
import com.example.shop.domain.product.service.ProductOptionService;
import com.example.shop.domain.product.service.ProductService;
import com.example.shop.domain.user.dto.response.UserSummary;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.entity.UserInfo;
import com.example.shop.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * product application service.
 * product / user / order domain service 를 조합하고, 각 domain 의 결과를 응답 DTO 로 mapping 한다.
 * domain 로직은 갖지 않는다.
 */
@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final ProductOptionService optionService;
    private final UserService userService;
    private final OrderService orderService;

    /** product-01 : 새 상품 등록 */
    @Transactional
    public ProductResponse create(Long userId, ProductCreateRequest request) {
        UserInfo creator = userService.getUser(userId);
        ProductInfo product = productService.create(creator.getId(), request);
        return product.toProductResponse(creator);
    }

    /** product-02 : 상품 정보 수정 (주문 존재 여부는 order domain 에게 물어본다) */
    @Transactional
    public ProductResponse update(Long userId, Long productId, ProductUpdateRequest request) {
        UserInfo user = userService.getUser(userId);
        boolean hasOrder = orderService.existsOrderForProduct(productId); // 다음 service에 필요한 내용을 facade가 알고있어야함

        ProductInfo product = productService.update(productId, user.getId(), request, hasOrder);
        return  product.toProductResponse(user);
    }

    /** product-03 : 상품 리스트 조회 */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<ProductInfo> productInfos = productService.getAllProducts();
        List<Long> creatorIds = productInfos.stream().map(ProductInfo::getCreatorId).toList();
        List<UserInfo> creatorInfos = userService.getUsers(creatorIds);

        return ProductInfo.toProductResponses(productInfos, creatorInfos);
    }

    /** product-05 : 내가 생성한 상품 리스트 조회 */
    @Transactional(readOnly = true)
    public List<ProductResponse> getMyProducts(Long userId) {
        UserInfo user = userService.getUser(userId);
        return productService.getMyProducts(user.getId()).stream()
                .map(productInfo -> productInfo.toProductResponse(user))
                .toList();
    }

    /** product-04 : 상품 상세 조회 (상품 + 옵션 + creator 이름을 조합) */
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long productId) {
        ProductInfo product = productService.getProduct(productId);
        UserInfo creator = userService.getUser(product.getCreatorId());
        List<OptionInfo> options = optionService.getOptionsByProduct(productId);

        return product.toProductDetailResponse(creator, options);
    }

}
