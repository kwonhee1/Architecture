package com.example.shop.product.application;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.product.application.dto.*;
import com.example.shop.product.application.port.OrderExistencePort;
import com.example.shop.product.domain.model.Option;
import com.example.shop.product.domain.model.Product;
import com.example.shop.product.domain.model.vo.CreatorId;
import com.example.shop.product.domain.model.vo.Money;
import com.example.shop.product.domain.model.vo.ProductId;
import com.example.shop.product.domain.repository.OptionRepository;
import com.example.shop.product.domain.repository.ProductRepository;
import com.example.shop.user.domain.model.vo.UserId;
import com.example.shop.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * product use case 흐름 조립.
 *
 * <p>생성자 이름은 기반 aggregate 인 user 를 읽어 채운다(원시값만 꺼낸다).
 * "order 존재 시 금액 수정 불가" 판정은 order 내부를 몰라도 되도록 {@link OrderExistencePort}
 * 계약으로만 물어본다.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductApplicationService {

    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final OrderExistencePort orderExistencePort;

    /**
     * product-01 : 새 상품 등록.
     * 기본 옵션 { "기본 옵션", 0, 재고 0 } 을 함께 생성한다. Product 와 Option 은 다른 aggregate 지만
     * 상품 등록과 기본 옵션 생성은 함께 성공/실패해야 하므로 한 트랜잭션에서 처리한다.
     */
    @Transactional
    public ProductInfo create(long creatorId, CreateProductCommand command) {
        Product product = Product.register(
                CreatorId.of(creatorId), command.description(), Money.of(command.price()));
        Product saved = productRepository.save(product);

        optionRepository.save(Option.createDefault(saved.id()));

        return ProductInfo.of(saved, creatorName(saved.creatorId()));
    }

    /** product-02 : 상품 정보 수정 (생성자만, order 존재 시 금액 수정 불가). */
    @Transactional
    public ProductInfo update(long productId, long requesterId, UpdateProductCommand command) {
        Product product = loadProduct(productId);
        product.validateOwner(CreatorId.of(requesterId));

        if (command.description() != null) {
            product.changeDescription(command.description());
        }
        if (command.price() != null) {
            if (orderExistencePort.existsForProduct(productId)) {
                throw new BusinessException(ErrorCode.PRICE_UPDATE_NOT_ALLOWED);
            }
            product.changePrice(Money.of(command.price()));
        }

        Product saved = productRepository.save(product);
        return ProductInfo.of(saved, creatorName(saved.creatorId()));
    }

    /** product-03 : 상품 리스트. */
    public List<ProductInfo> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> ProductInfo.of(p, creatorName(p.creatorId())))
                .toList();
    }

    /** product-05 : 내가 생성한 상품 리스트. */
    public List<ProductInfo> getMyProducts(long creatorId) {
        String name = creatorName(CreatorId.of(creatorId));
        return productRepository.findByCreatorId(CreatorId.of(creatorId)).stream()
                .map(p -> ProductInfo.of(p, name))
                .toList();
    }

    /** product-04 : 상품 상세 (옵션 포함). */
    public ProductDetailInfo getProductDetail(long productId) {
        Product product = loadProduct(productId);
        List<OptionInfo> options = optionRepository.findByProductId(ProductId.of(productId)).stream()
                .map(OptionInfo::from)
                .toList();
        return new ProductDetailInfo(
                product.id().value(),
                product.description(),
                product.price().value(),
                creatorName(product.creatorId()),
                options
        );
    }

    private Product loadProduct(long productId) {
        return productRepository.findById(ProductId.of(productId))
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private String creatorName(CreatorId creatorId) {
        return userRepository.findById(UserId.of(creatorId.value()))
                .map(u -> u.name())
                .orElse(null);
    }
}
