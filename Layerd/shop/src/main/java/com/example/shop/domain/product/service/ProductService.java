package com.example.shop.domain.product.service;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.domain.option.entity.ProductOption;
import com.example.shop.domain.option.repository.ProductOptionRepository;
import com.example.shop.domain.order.repository.OrderItemRepository;
import com.example.shop.domain.product.dto.ProductCreateRequest;
import com.example.shop.domain.product.dto.ProductDetailResponse;
import com.example.shop.domain.product.dto.ProductResponse;
import com.example.shop.domain.product.dto.ProductUpdateRequest;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.repository.ProductRepository;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private static final String DEFAULT_OPTION_DESCRIPTION = "기본 옵션";

    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;

    /** product-01 : 새 상품 등록 (기본 옵션 함께 생성) */
    @Transactional
    public ProductResponse create(Long userId, ProductCreateRequest request) {
        User creator = userService.getUser(userId);
        Product product = productRepository.save(Product.builder()
                .creator(creator)
                .description(request.description())
                .price(request.price())
                .build());

        optionRepository.save(ProductOption.builder()
                .product(product)
                .description(DEFAULT_OPTION_DESCRIPTION)
                .additionalPrice(0)
                .stock(0)
                .build());

        return ProductResponse.from(product);
    }

    /** product-03 : 상품 리스트 조회 */
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    /** product-05 : 내가 생성한 상품 리스트 조회 */
    public List<ProductResponse> getMyProducts(Long userId) {
        return productRepository.findByCreatorId(userId).stream()
                .map(ProductResponse::from)
                .toList();
    }

    /** product-04 : 상품 상세 조회 (옵션 포함) */
    public ProductDetailResponse getProductDetail(Long productId) {
        Product product = getProduct(productId);
        List<ProductOption> options = optionRepository.findByProductIdWithProduct(productId);
        return ProductDetailResponse.of(product, options);
    }

    /** product-02 : 상품 정보 수정 (수정할 값만, order 존재 시 금액 수정 불가, 생성자만) */
    @Transactional
    public ProductResponse update(Long productId, Long userId, ProductUpdateRequest request) {
        Product product = getProduct(productId);
        product.validateOwner(userId);

        if (request.description() != null) {
            product.changeDescription(request.description());
        }
        if (request.price() != null) {
            if (orderItemRepository.existsByProductId(productId)) {
                throw new BusinessException(ErrorCode.PRICE_UPDATE_NOT_ALLOWED);
            }
            product.changePrice(request.price());
        }
        return ProductResponse.from(product);
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public int purchase(Long optionId, int quantity) {
        ProductOption option = optionRepository.findById(optionId)
                .orElseThrow(()->new BusinessException(ErrorCode.OPTION_NOT_FOUND));
        option.decreaseStock(quantity);

        int amount = (option.getAdditionalPrice() + option.getProduct().getPrice()) * quantity;
        return amount;
    }
}
