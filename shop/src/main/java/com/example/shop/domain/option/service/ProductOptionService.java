package com.example.shop.domain.option.service;

import com.example.shop.domain.option.dto.ProductOptionRequest;
import com.example.shop.domain.option.dto.ProductOptionResponse;
import com.example.shop.domain.option.entity.ProductOption;
import com.example.shop.domain.option.repository.ProductOptionRepository;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductOptionService {

    private final ProductOptionRepository optionRepository;
    private final ProductService productService;

    @Transactional
    public ProductOptionResponse create(Long productId, Long userId, ProductOptionRequest request) {
        Product product = productService.getProduct(productId);
        product.validateCreator(userId);

        ProductOption option = ProductOption.builder()
                .product(product)
                .name(request.name())
                .additionalPrice(request.additionalPrice())
                .stock(request.stock())
                .build();
        return ProductOptionResponse.from(optionRepository.save(option));
    }

    public List<ProductOptionResponse> getByProduct(Long productId) {
        return optionRepository.findByProductId(productId).stream()
                .map(ProductOptionResponse::from)
                .toList();
    }

    @Transactional
    public ProductOptionResponse update(Long optionId, ProductOptionRequest request, Long userId) {
        ProductOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new NoSuchElementException("옵션을 찾을 수 없습니다."));
        Product product = option.getProduct();
        product.validateCreator(userId);

        option.update(request.name(), request.additionalPrice(), request.stock());
        return ProductOptionResponse.from(option);
    }

    @Transactional
    public void delete(Long optionId) {
        if (!optionRepository.existsById(optionId)) {
            throw new NoSuchElementException("옵션을 찾을 수 없습니다.");
        }
        optionRepository.deleteById(optionId);
    }

    public ProductOption getEntity(Long optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new NoSuchElementException("옵션을 찾을 수 없습니다."));
    }
}
