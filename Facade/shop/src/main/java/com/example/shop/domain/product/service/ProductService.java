package com.example.shop.domain.product.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.order.dto.OrderItemResponse;
import com.example.shop.domain.order.entity.OrderItemInfo;
import com.example.shop.domain.product.dto.*;
import com.example.shop.domain.product.dto.request.ProductCreateRequest;
import com.example.shop.domain.product.dto.request.ProductUpdateRequest;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.entity.ProductInfo;
import com.example.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * product domain service. product domain 의 로직만 담당하며,
 * 다른 domain(user / order)의 service 는 호출하지 않는다.
 * ProductOptionService 는 같은 domain(product, option) 이므로 호출할 수 있다.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionService optionService;

    /** product-01 : 새 상품 등록 (기본 옵션 함께 생성) */
    public ProductInfo create(Long userId, ProductCreateRequest request) {
        Product product = productRepository.save(Product.builder()
                .creatorId(userId)
                .description(request.description())
                .price(request.price())
                .build());

        optionService.createDefaultOption(product);
        return ProductInfo.from(product);
    }

    /**
     * product-02 : 상품 정보 수정 (수정할 값만, order 존재 시 금액 수정 불가, 생성자만).
     * 주문 존재 여부(hasOrder)는 order domain 의 사실이므로 facade 가 넘겨준다.
     */
    public ProductInfo update(Long productId, Long userId, ProductUpdateRequest request,
                              boolean hasOrder) {
        Product product = getEntity(productId);
        product.validateOwner(userId);

        if (request.description() != null) {
            product.changeDescription(request.description());
        }
        if (request.price() != null) {
            if (hasOrder) {
                throw new BusinessException(ErrorCode.PRICE_UPDATE_NOT_ALLOWED);
            }
            product.changePrice(request.price());
        }
        return ProductInfo.from(productRepository.save(product));
    }

    /** product-03 : 상품 리스트 조회 */
    public List<ProductInfo> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductInfo::from)
                .toList();
    }

    /** product-05 : 내가 생성한 상품 리스트 조회 */
    public List<ProductInfo> getMyProducts(Long userId) {
        return productRepository.findByCreatorId(userId).stream()
                .map(ProductInfo::from)
                .toList();
    }

    public ProductInfo getProduct(Long productId) {
        return ProductInfo.from(getEntity(productId));
    }

    public List<ProductInfo> getProducts(Collection<Long> productIds) {
        return productRepository.findAllById(productIds).stream()
                .map(ProductInfo::from)
                .toList();
    }

    /** order-03 : 판매자(상품 생성자)만 조회 가능 */
    public ProductInfo validateOwner(Long productId, Long userId) {
        Product product = getEntity(productId);
        if (!product.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_PRODUCT_OWNER);
        }
        return ProductInfo.from(product);
    }

    /**
     * order-01 : 구매 처리. 재고 차감과 금액 계산을 product domain 안에서 끝내고,
     * order 가 주문을 만드는 데 필요한 값만 돌려준다.
     */
    public List<PurchaseResult> purchase(List<OrderItemResponse> items) {
        return items.stream()
                .map(item -> optionService.purchase(item.optionId(), item.count()))
                .toList();
    }

    /** order-04 : 주문 취소 시 재고 복원 (취소된 주문의 주문 줄 그대로) */
    public void restore(List<OrderItemInfo> items) {
        items.forEach(item -> optionService.restore(item.getOptionId(), item.getCount()));
    }

    private Product getEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
