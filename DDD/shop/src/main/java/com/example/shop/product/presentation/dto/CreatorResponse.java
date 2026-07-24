package com.example.shop.product.presentation.dto;

/** 생성자 정보 { name } */
public record CreatorResponse(String name) {
    public static CreatorResponse of(String name) {
        return new CreatorResponse(name);
    }
}
