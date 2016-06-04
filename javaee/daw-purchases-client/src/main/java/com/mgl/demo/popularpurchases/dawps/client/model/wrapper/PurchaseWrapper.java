package com.mgl.demo.popularpurchases.dawps.client.model.wrapper;

import com.mgl.demo.popularpurchases.dawps.client.model.wrapper.support.BaseItemWrapper;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgl.demo.popularpurchases.dawps.client.model.Purchase;

public class PurchaseWrapper extends BaseItemWrapper<Purchase> {

    @JsonCreator
    public PurchaseWrapper(@JsonProperty("purchase") final Purchase purchase) {
        super(purchase);
    }

}
