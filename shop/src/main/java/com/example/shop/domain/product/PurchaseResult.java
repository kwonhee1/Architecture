package com.example.shop.domain.product;

import com.example.shop.domain.option.entity.ProductOption;
import com.example.shop.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PurchaseResult {
    private Product product;
    private ProductOption option;
    private int count;

    private int purchasePrice;
}
