package com.example.shop.domain.product.facade;

import com.example.shop.domain.order.service.OrderService;
import com.example.shop.domain.product.entity.OptionInfo;
import com.example.shop.domain.product.dto.request.ProductOptionCreateRequest;
import com.example.shop.domain.product.dto.request.ProductOptionUpdateRequest;
import com.example.shop.domain.product.service.ProductOptionService;
import com.example.shop.domain.user.entity.UserInfo;
import com.example.shop.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** option application service */
@Service
@RequiredArgsConstructor
public class ProductOptionFacade {

    private final ProductOptionService optionService;
    private final UserService userService;
    private final OrderService orderService;

    /** option-01 : 옵션 생성 */
    @Transactional
    public OptionInfo create(Long userId, Long productId, ProductOptionCreateRequest request) {
        UserInfo user = userService.getUser(userId);
        return optionService.create(productId, user.getId(), request);
    }

    /** option-02 : 옵션 수정 (주문 존재 여부는 order domain 에게 물어본다) */
    @Transactional
    public OptionInfo update(Long userId, Long optionId, ProductOptionUpdateRequest request) {
        UserInfo user = userService.getUser(userId);
        boolean hasOrder = orderService.existsOrderForOption(optionId);
        return optionService.update(optionId, user.getId(), request, hasOrder);
    }

    /** option-02 : 옵션 삭제 */
    @Transactional
    public void delete(Long userId, Long optionId) {
        UserInfo user = userService.getUser(userId);
        boolean hasOrder = orderService.existsOrderForOption(optionId);
        optionService.delete(optionId, user.getId(), hasOrder);
    }
}
