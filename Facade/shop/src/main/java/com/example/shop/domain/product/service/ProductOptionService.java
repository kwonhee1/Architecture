package com.example.shop.domain.product.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.product.dto.*;
import com.example.shop.domain.product.dto.request.ProductOptionCreateRequest;
import com.example.shop.domain.product.dto.request.ProductOptionUpdateRequest;
import com.example.shop.domain.product.entity.OptionInfo;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.entity.ProductOption;
import com.example.shop.domain.product.repository.ProductOptionRepository;
import com.example.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * option domain service. option 은 product 와 같은 domain 이다.
 * 다른 domain(user / order)의 service 는 호출하지 않으며,
 * 필요한 사실(예: 주문 존재 여부)은 facade 가 파라미터로 넘겨준다.
 */
@Service
@RequiredArgsConstructor
public class ProductOptionService {

    private static final String DEFAULT_OPTION_DESCRIPTION = "기본 옵션";

    private final ProductOptionRepository optionRepository;
    private final ProductRepository productRepository;

    /** product-01 : 상품 등록 시 함께 생성되는 기본 옵션 { "기본 옵션", 0, 재고 0 } */
    void createDefaultOption(Product product) {
        optionRepository.save(ProductOption.builder()
                .product(product)
                .description(DEFAULT_OPTION_DESCRIPTION)
                .additionalPrice(0)
                .stock(0)
                .build());
    }

    /** option-01 : 옵션 생성 (상품 생성자만, 추가금액 + 상품금액 > 0, 재고 >= 0) */
    public OptionInfo create(Long productId, Long userId, ProductOptionCreateRequest request) {
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
        return OptionInfo.from(option);
    }

    /**
     * option-02 : 옵션 수정 (상품 생성자만, 수정할 값만 전달).
     * 해당 옵션의 order 가 존재하면 재고 외에는 수정할 수 없다.
     */
    public OptionInfo update(Long optionId, Long userId, ProductOptionUpdateRequest request,
                             boolean hasOrder) {
        ProductOption option = getEntity(optionId);
        option.getProduct().validateOwner(userId);

        boolean editsNonStock = request.description() != null || request.additionalPrice() != null;
        if (hasOrder && editsNonStock) {
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
        return OptionInfo.from(optionRepository.save(option));
    }

    /** option-02 : 옵션 삭제 (상품 생성자만, order 존재 시 삭제 불가) */
    public void delete(Long optionId, Long userId, boolean hasOrder) {
        ProductOption option = getEntity(optionId);
        option.getProduct().validateOwner(userId);

        if (hasOrder) {
            throw new BusinessException(ErrorCode.ALREADY_HAS_ORDER);
        }
        optionRepository.delete(option);
    }

    public List<OptionInfo> getOptions(Collection<Long> optionIds) {
        return optionRepository.findByIdIn(optionIds).stream()
                .map(OptionInfo::from)
                .toList();
    }

    /** product-04 : 상품 상세의 옵션 리스트 */
    public List<OptionInfo> getOptionsByProduct(Long productId) {
        return optionRepository.findByProductId(productId).stream()
                .map(OptionInfo::from)
                .toList();
    }

    /** order-01 : 재고 차감 + 구매 금액 계산 */
    PurchaseResult purchase(Long optionId, int count) {
        ProductOption option = getEntity(optionId);
        option.decreaseStock(count);
        return new PurchaseResult(
                option.getProduct().getId(),
                option.getId(),
                count,
                option.purchasePrice(count)
        );
    }

    /** order-04 : 재고 복원 */
    void restore(Long optionId, int count) {
        getEntity(optionId).increaseStock(count);
    }

    private ProductOption getEntity(Long optionId) {
        return optionRepository.findByIdWithProduct(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
    }
}
