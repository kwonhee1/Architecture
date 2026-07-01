package com.example.shop.domain.product.service;

import com.example.shop.domain.option.entity.ProductOption;
import com.example.shop.domain.product.PurchaseResult;
import com.example.shop.domain.product.dto.ProductCreateRequest;
import com.example.shop.domain.product.dto.ProductResponse;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.entity.ProductStatus;
import com.example.shop.domain.product.repository.ProductRepository;
import com.example.shop.domain.user.entity.User;
import com.example.shop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProductResponse create(Long userId, ProductCreateRequest request) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        Product product = Product.builder()
                .creator(creator)
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .build();
        return ProductResponse.from(productRepository.save(product));
    }

    public List<ProductResponse> getAll() {
        return productRepository.findByStatus(ProductStatus.ON_SALE).stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse update(Long productId, Long userId, ProductCreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        product.validateCreator(userId);
        product.update(request.name(), request.price(), request.description());
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        product.validateCreator(userId);
        product.changeStatus(ProductStatus.DISCONTINUED);
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
    }

    public PurchaseResult purchase(Product product, ProductOption option, int count) {
        if (option != null) option.decreaseStock(count);
        int price = (product.getPrice() + (option != null ? option.getAdditionalPrice() : 0)) * count;

        return new PurchaseResult(product, option, count, price);
    }
}
