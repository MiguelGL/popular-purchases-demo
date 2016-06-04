package com.mgl.demo.popularpurchases.dawps.client.model.wrapper;

import com.mgl.demo.popularpurchases.dawps.client.model.wrapper.support.BaseListWrapper;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgl.demo.popularpurchases.dawps.client.model.Product;

public class ProductsWrapper extends BaseListWrapper<Product> {

    @JsonCreator
    public ProductsWrapper(@JsonProperty("products") final List<Product> products) {
        super(products);
    }

}
