package com.example.shop.product.application.dto;

public record CreateOptionCommand(String description, long additionalPrice, long stock) {
}
