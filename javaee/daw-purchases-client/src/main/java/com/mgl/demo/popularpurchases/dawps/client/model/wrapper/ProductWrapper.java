package com.mgl.demo.popularpurchases.dawps.client.model.wrapper;

import com.mgl.demo.popularpurchases.dawps.client.model.wrapper.support.BaseItemWrapper;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgl.demo.popularpurchases.dawps.client.model.Product;

public class ProductWrapper extends BaseItemWrapper<Product> {

    @JsonCreator
    public ProductWrapper(@JsonProperty("product") final Product product) {
        super(product);
    }

}
