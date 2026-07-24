package com.example.shop.product.application;

import com.example.shop.common.exception.BusinessException;
import com.example.shop.common.exception.ErrorCode;
import com.example.shop.product.application.dto.CreateOptionCommand;
import com.example.shop.product.application.dto.OptionInfo;
import com.example.shop.product.application.dto.UpdateOptionCommand;
import com.example.shop.product.application.port.OrderExistencePort;
import com.example.shop.product.domain.model.Option;
import com.example.shop.product.domain.model.Product;
import com.example.shop.product.domain.model.vo.CreatorId;
import com.example.shop.product.domain.model.vo.OptionId;
import com.example.shop.product.domain.model.vo.ProductId;
import com.example.shop.product.domain.repository.OptionRepository;
import com.example.shop.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * option use case 흐름 조립.
 *
 * <p>소유자 확인·가격 검증은 상품 금액이 필요하므로 Product 를 읽어 값을 넘기고, 판정 자체는
 * 각 aggregate(Product·Option) 가 한다. "order 존재 시 재고 외 수정/삭제 불가" 는
 * {@link OrderExistencePort} 로만 물어본다.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OptionApplicationService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;
    private final OrderExistencePort orderExistencePort;

    /** option-01 : 옵션 생성 (상품 생성자만). */
    @Transactional
    public OptionInfo create(long productId, long requesterId, CreateOptionCommand command) {
        Product product = loadProduct(productId);
        product.validateOwner(CreatorId.of(requesterId));

        Option option = Option.create(
                ProductId.of(productId),
                command.description(),
                command.additionalPrice(),
                command.stock(),
                product.price()
        );
        return OptionInfo.from(optionRepository.save(option));
    }

    /**
     * option-02 : 옵션 수정 (상품 생성자만).
     * order 가 존재하면 재고 외 항목(설명·추가금액)은 수정할 수 없다.
     */
    @Transactional
    public OptionInfo update(long optionId, long requesterId, UpdateOptionCommand command) {
        Option option = loadOption(optionId);
        Product product = loadProduct(option.productId().value());
        product.validateOwner(CreatorId.of(requesterId));

        boolean editsNonStock = command.description() != null || command.additionalPrice() != null;
        if (editsNonStock && orderExistencePort.existsForOption(optionId)) {
            throw new BusinessException(ErrorCode.ALREADY_HAS_ORDER);
        }

        if (command.description() != null) {
            option.changeDescription(command.description());
        }
        if (command.additionalPrice() != null) {
            option.changeAdditionalPrice(command.additionalPrice(), product.price());
        }
        if (command.stock() != null) {
            option.changeStock(command.stock());
        }
        return OptionInfo.from(optionRepository.save(option));
    }

    /** option-02 : 옵션 삭제 (상품 생성자만, order 존재 시 삭제 불가). */
    @Transactional
    public void delete(long optionId, long requesterId) {
        Option option = loadOption(optionId);
        Product product = loadProduct(option.productId().value());
        product.validateOwner(CreatorId.of(requesterId));

        if (orderExistencePort.existsForOption(optionId)) {
            throw new BusinessException(ErrorCode.ALREADY_HAS_ORDER);
        }
        optionRepository.delete(option);
    }

    private Product loadProduct(long productId) {
        return productRepository.findById(ProductId.of(productId))
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private Option loadOption(long optionId) {
        return optionRepository.findById(OptionId.of(optionId))
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
    }
}
