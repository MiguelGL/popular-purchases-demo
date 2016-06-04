package com.mgl.demo.popularpurchases.dawps.client;

import lombok.Getter;

public class ProductNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    @Getter
    private final long productId;

    public ProductNotFoundException(long productId) {
        super(String.format("Product with id '%d' does not exist", productId));
        this.productId = productId;
    }

}
