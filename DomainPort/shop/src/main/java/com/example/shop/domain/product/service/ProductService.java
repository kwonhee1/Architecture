package com.example.shop.domain.product.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.order.service.port.OrderDomainPort;
import com.example.shop.domain.product.dto.ProductCreateRequest;
import com.example.shop.domain.product.dto.ProductDetailResponse;
import com.example.shop.domain.product.dto.ProductOptionResponse;
import com.example.shop.domain.product.dto.ProductResponse;
import com.example.shop.domain.product.dto.ProductUpdateRequest;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.entity.ProductOption;
import com.example.shop.domain.product.repository.ProductOptionRepository;
import com.example.shop.domain.product.repository.ProductRepository;
import com.example.shop.domain.product.service.port.ProductDomainPort;
import com.example.shop.domain.product.service.usecase.ProductUseCase;
import com.example.shop.domain.product.service.vo.ProductVo.*;
import com.example.shop.domain.user.dto.CreatorResponse;
import com.example.shop.domain.user.service.port.UserDomainPort;
import com.example.shop.domain.user.service.vo.UserVo.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * product domain 의 로직 소유자.
 * <p>
 * 다른 domain 은 port 로만 쓴다 — user 는 {@link UserDomainPort}, order 는 {@link OrderDomainPort}.
 * UserService / OrderService 를 직접 주입받지 않는다.
 * <p>
 * option 은 같은 domain 이므로 repository 를 직접 다룬다.
 * <p>
 * controller 로는 dto, 다른 domain 으로는 VO 를 내보낸다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService implements ProductUseCase, ProductDomainPort {

    private static final String DEFAULT_OPTION_DESCRIPTION = "기본 옵션";

    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;
    private final UserDomainPort userPort;

    /** order 도 product 를 port 로 사용하므로 빈 생성 순환을 피해 지연 주입한다. */
    @Lazy
    private final OrderDomainPort orderPort;

    // ────────────── UseCase (controller 전용) ──────────────

    /** product-01 : 새 상품 등록 (기본 옵션 { "기본 옵션", 0, 재고 0 } 함께 생성) */
    @Override
    @Transactional
    public ProductResponse create(Long userId, ProductCreateRequest request) {
        // 생성자가 실재하는지 확인 + 응답에 넣을 이름 확보 (User entity 는 넘어오지 않는다)
        UserInfo creator = userPort.getUserInfo(userId);

        Product product = productRepository.save(Product.builder()
                .creatorId(userId)
                .description(request.description())
                .price(request.price())
                .build());

        optionRepository.save(ProductOption.builder()
                .product(product)
                .description(DEFAULT_OPTION_DESCRIPTION)
                .additionalPrice(0)
                .stock(0)
                .build());

        return toResponse(product, creator);
    }

    /** product-03 : 상품 리스트 조회 */
    @Override
    public List<ProductResponse> getAllProducts() {
        return toResponses(productRepository.findAll());
    }

    /** product-05 : 내가 생성한 상품 리스트 조회 */
    @Override
    public List<ProductResponse> getMyProducts(Long userId) {
        return toResponses(productRepository.findByCreatorId(userId));
    }

    /** product-04 : 상품 상세 조회 (옵션 포함) */
    @Override
    public ProductDetailResponse getProductDetail(Long productId) {
        Product product = getEntity(productId);
        // option 은 같은 domain 이라 entity 에서 바로 응답 모양으로 옮긴다
        List<ProductOptionResponse> options = optionRepository.findByProductIdWithProduct(productId).stream()
                .map(ProductOptionResponse::from)
                .toList();

        return new ProductDetailResponse(
                product.getId(),
                product.getDescription(),
                product.getPrice(),
                new CreatorResponse(creatorOf(product).name()),
                options);
    }

    /** product-02 : 상품 정보 수정 (생성자만, order 존재 시 금액 수정 불가) */
    @Override
    @Transactional
    public ProductResponse update(Long productId, Long userId, ProductUpdateRequest request) {
        Product product = getEntity(productId);
        product.validateOwner(userId);

        if (request.description() != null) {
            product.changeDescription(request.description());
        }
        if (request.price() != null) {
            // 주문이 있는지는 order 가 답한다 (order 의 repository 를 직접 뒤지지 않는다)
            if (orderPort.existsByProduct(productId)) {
                throw new BusinessException(ErrorCode.PRICE_UPDATE_NOT_ALLOWED);
            }
            product.changePrice(request.price());
        }
        return toResponse(product, creatorOf(product));
    }

    // ────────────── DomainPort (타 domain 전용) ──────────────

    @Override
    public List<ProductInfo> getProductInfos(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, UserInfo> creators = creatorsOf(products);

        return products.stream()
                .map(p -> ProductInfo.of(p, creators.get(p.getCreatorId())))
                .toList();
    }

    /** 판매자(생성자)가 맞는지는 product 가 판단한다 */
    @Override
    public boolean checkOwner(Long productId, Long userId) {
        return getEntity(productId).isCreator(userId);
    }

    // ────────────── 내부 ──────────────

    private Product getEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private List<ProductResponse> toResponses(List<Product> products) {
        Map<Long, UserInfo> creators = creatorsOf(products);
        return products.stream()
                .map(p -> toResponse(p, creators.get(p.getCreatorId())))
                .toList();
    }

    /** 생성자들을 한 번에 받아 productId 매칭용으로 id 별로 세워 둔다 */
    private Map<Long, UserInfo> creatorsOf(List<Product> products) {
        List<Long> creatorIds = products.stream()
                .map(Product::getCreatorId)
                .distinct()
                .toList();
        return userPort.getUserInfos(creatorIds).stream()
                .collect(Collectors.toMap(UserInfo::id, Function.identity()));
    }

    private UserInfo creatorOf(Product product) {
        return userPort.getUserInfo(product.getCreatorId());
    }

    private ProductResponse toResponse(Product product, UserInfo creator) {
        return ProductResponse.from(ProductInfo.of(product, creator));
    }
}
