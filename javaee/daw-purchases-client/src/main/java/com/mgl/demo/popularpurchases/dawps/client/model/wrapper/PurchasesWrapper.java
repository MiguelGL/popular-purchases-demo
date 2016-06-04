package com.mgl.demo.popularpurchases.dawps.client.model.wrapper;

import com.mgl.demo.popularpurchases.dawps.client.model.wrapper.support.BaseListWrapper;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgl.demo.popularpurchases.dawps.client.model.Purchase;

public class PurchasesWrapper extends BaseListWrapper<Purchase> {

    @JsonCreator
    public PurchasesWrapper(@JsonProperty("purchases") final List<Purchase> purchases) {
        super(purchases);
    }

}
