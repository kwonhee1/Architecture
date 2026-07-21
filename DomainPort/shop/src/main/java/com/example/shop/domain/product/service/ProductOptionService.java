package com.example.shop.domain.product.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.order.service.port.OrderDomainPort;
import com.example.shop.domain.product.dto.ProductOptionCreateRequest;
import com.example.shop.domain.product.dto.ProductOptionResponse;
import com.example.shop.domain.product.dto.ProductOptionUpdateRequest;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.entity.ProductOption;
import com.example.shop.domain.product.repository.ProductOptionRepository;
import com.example.shop.domain.product.repository.ProductRepository;
import com.example.shop.domain.product.service.port.ProductOptionDomainPort;
import com.example.shop.domain.product.service.usecase.ProductOptionUseCase;
import com.example.shop.domain.product.service.vo.ProductOptionVo.*;
import com.example.shop.domain.product.service.vo.ProductVo.ProductInfo;
import com.example.shop.domain.user.service.port.UserDomainPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * option 의 로직 소유자.
 * <p>
 * product 는 같은 domain 이므로 repository 를 직접 다루고, order 는 다른 domain 이므로
 * {@link OrderDomainPort} 로만 접근한다.
 * <p>
 * controller 로는 dto, 다른 domain 으로는 VO 를 내보낸다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductOptionService implements ProductOptionUseCase, ProductOptionDomainPort {

    private final ProductOptionRepository optionRepository;
    private final ProductRepository productRepository;

    /** 결과 VO 의 상품 정보에 실을 생성자 이름을 채우기 위해 쓴다 (User entity 는 넘어오지 않는다) */
    private final UserDomainPort userPort;

    /** order 도 option 을 port 로 사용하므로 빈 생성 순환을 피해 지연 주입한다. */
    @Lazy
    private final OrderDomainPort orderPort;

    // ────────────── UseCase (controller 전용) ──────────────

    /** option-01 : 옵션 생성 (상품 생성자만, 추가금액 + 상품금액 > 0, 재고 >= 0) */
    @Override
    @Transactional
    public ProductOptionResponse create(Long productId, Long userId, ProductOptionCreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        product.validateOwner(userId);

        if (product.getPrice() + request.additionalPrice() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_OPTION_PRICE);
        }
        if (request.stock() < 0) {
            throw new BusinessException(ErrorCode.INVALID_OPTION_STOCK);
        }

        ProductOption option = optionRepository.save(ProductOption.builder()
                .product(product)
                .description(request.description())
                .additionalPrice(request.additionalPrice())
                .stock(request.stock())
                .build());
        return ProductOptionResponse.from(option);
    }

    /**
     * option-02 : 옵션 수정 (상품 생성자만).
     * 해당 옵션의 order 가 존재하면 재고(stock) 외 항목은 수정할 수 없다.
     */
    @Override
    @Transactional
    public ProductOptionResponse update(Long optionId, Long userId, ProductOptionUpdateRequest request) {
        ProductOption option = getEntity(optionId);
        option.getProduct().validateOwner(userId);

        boolean editsNonStock = request.description() != null || request.additionalPrice() != null;
        if (editsNonStock && hasOrder(optionId)) {
            throw new BusinessException(ErrorCode.ALREADY_HAS_ORDER);
        }

        if (request.description() != null) {
            option.changeDescription(request.description());
        }
        if (request.additionalPrice() != null) {
            option.changeAdditionalPrice(request.additionalPrice());
        }
        if (request.stock() != null) {
            option.changeStock(request.stock());
        }
        return ProductOptionResponse.from(option);
    }

    /** option-02 : 옵션 삭제 (상품 생성자만, order 존재 시 삭제 불가) */
    @Override
    @Transactional
    public void delete(Long optionId, Long userId) {
        ProductOption option = getEntity(optionId);
        option.getProduct().validateOwner(userId);

        if (hasOrder(optionId)) {
            throw new BusinessException(ErrorCode.ALREADY_HAS_ORDER);
        }
        optionRepository.delete(option);
    }

    // ────────────── DomainPort (타 domain 전용) ──────────────

    @Override
    public List<OptionInfo> getOptionInfos(List<Long> optionIds) {
        if (optionIds == null || optionIds.isEmpty()) {
            return List.of();
        }
        return optionRepository.findByIdInWithProduct(optionIds).stream()
                .map(OptionInfo::of)
                .toList();
    }

    /**
     * 재고 차감. 재고가 충분한지의 판단도, 단가 계산도 여기(option)에서 끝난다.
     * order 는 결과 VO 가 설명해 주는 값만 받아 간다. 부족하면 예외 → 호출 트랜잭션 롤백.
     */
    @Override
    @Transactional
    public StockDecreaseResult decreaseStock(Long optionId, int count) {
        ProductOption option = getEntity(optionId);
        option.decreaseStock(count);

        return new StockDecreaseResult(
                OptionInfo.of(option),
                productInfoOf(option),
                count,
                option.unitPrice() * count,
                option.getStock());
    }

    @Override
    @Transactional
    public StockRestoreResult restoreStock(Long optionId, int count) {
        ProductOption option = getEntity(optionId);
        option.increaseStock(count);
        return new StockRestoreResult(
                productInfoOf(option),
                OptionInfo.of(option),
                count,
                option.getStock());
    }

    // ────────────── 내부 ──────────────

    private ProductOption getEntity(Long optionId) {
        return optionRepository.findByIdWithProduct(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
    }

    private boolean hasOrder(Long optionId) {
        return orderPort.existsByOption(optionId);
    }

    /** product 는 같은 domain 이지만 생성자 이름은 user 의 값이라 port 로 물어 채운다 */
    private ProductInfo productInfoOf(ProductOption option) {
        Product product = option.getProduct();
        return ProductInfo.of(product, userPort.getUserInfo(product.getCreatorId()));
    }

}
