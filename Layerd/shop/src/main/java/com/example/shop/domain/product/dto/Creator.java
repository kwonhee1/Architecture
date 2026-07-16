package com.example.shop.domain.product.dto;

import com.example.shop.domain.user.entity.User;

/** 생성자 정보 { name } */
public record Creator(String name) {
    public static Creator from(User user) {
        return new Creator(user.getName());
    }
}