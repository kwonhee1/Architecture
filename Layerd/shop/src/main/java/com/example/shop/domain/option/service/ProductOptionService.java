package com.example.shop.domain.option.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.option.dto.ProductOptionCreateRequest;
import com.example.shop.domain.option.dto.ProductOptionResponse;
import com.example.shop.domain.option.dto.ProductOptionUpdateRequest;
import com.example.shop.domain.option.entity.ProductOption;
import com.example.shop.domain.option.repository.ProductOptionRepository;
import com.example.shop.domain.order.repository.OrderItemRepository;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductOptionService {

    private final ProductOptionRepository optionRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;

    /** option-01 : 옵션 생성 (상품 생성자만, 추가금액+상품금액 > 0, 재고 >= 0) */
    @Transactional
    public ProductOptionResponse create(Long productId, Long userId, ProductOptionCreateRequest request) {
        Product product = productService.getProduct(productId);
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
     * option-02 : 옵션 수정 (상품 생성자만, 수정할 값만 전달).
     * 해당 옵션의 order가 존재하면 재고(stock) 외 항목은 수정할 수 없다.
     */
    @Transactional
    public ProductOptionResponse update(Long optionId, Long userId,
                                        ProductOptionUpdateRequest request) {
        ProductOption option = getEntityWithProduct(optionId);
        option.getProduct().validateOwner(userId);

        boolean hasOrder = orderItemRepository.existsByOptionId(optionId);
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
        return ProductOptionResponse.from(option);
    }

    /** option-02 : 옵션 삭제 (상품 생성자만, order 존재 시 삭제 불가) */
    @Transactional
    public void delete(Long optionId, Long userId) {
        ProductOption option = getEntityWithProduct(optionId);
        option.getProduct().validateOwner(userId);

        if (orderItemRepository.existsByOptionId(optionId)) {
            throw new BusinessException(ErrorCode.ALREADY_HAS_ORDER);
        }
        optionRepository.delete(option);
    }

    public ProductOption getEntityWithProduct(Long optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
    }
}
